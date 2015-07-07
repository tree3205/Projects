import sys
import importlib
import os
import cPickle as pickle
import logging

import zerorpc
import gevent
from gevent.queue import Queue



# Usage:
# python mr_worker.py <ip_address_master:port> <ip_address_worker:port>
# For example: python mr_worker.py 0.0.0.0:3205 0.0.0.0:3207

STATE_READY = 'READY'
debug = False
test_failure = False


class Worker(object):
    def __init__(self, master_ip_port, worker_ip_port):
        self.state = STATE_READY
        self.master_ip_port = master_ip_port
        self.worker_ip_port = worker_ip_port
        self.map_object = None
        self.reduce_object = None
        self.unit = None
        self.data_dir = None
        self.reducer_map_queue = Queue()
        self.master_c = None
        gevent.spawn(self.controller)

    def controller(self):
        while True:
            print('[Worker: %s]: %s' % (self.worker_ip_port, self.state))
            gevent.sleep(1)

    def ping(self):
        print('[Worker] Ping from Master')
        return True

    # Methods for mapper
    def do_map(self, split_info, task_file, mapper_id, num_mapper, num_reducer, task_name, file_info, split_size):
        gevent.spawn(self.do_map_async, split_info,
                     task_file, mapper_id, num_mapper, num_reducer, task_name, file_info, split_size)

    def do_map_async(self, split_info,
                     task_file, mapper_id, num_mapper, num_reducer, task_name, file_info, split_size):
        print "Get task %s from master, current mapper id is %d" % (task_name, mapper_id)
        self.read_map_task(task_file, mapper_id, num_mapper, num_reducer, task_name, split_size)
        print "Task %s M %d: read user map function successfully" % (task_name, mapper_id)
        data = self.read_input(split_info, mapper_id, num_mapper, file_info, split_size)
        print "Task %s M %d: read input successfully" % (task_name, mapper_id)

        # Ensure worker can get master ping
        # gevent.sleep(0)

        self.map_object.map(task_file[0], data)
        print "Task %s M %d: do map successfully" % (task_name, mapper_id)
        if test_failure:
            gevent.sleep(3)
        self.map_object.combine()
        print "Task %s M %d: combine intermediate result successfully" % (task_name, mapper_id)

        # Ensure worker can get master ping
        # gevent.sleep(0)

        self.map_object.write_to_file()
        print "Task %s M %d: write result to file successfully" % (task_name, mapper_id)
        temp_script = task_file[0] + "m" + str(mapper_id)
        os.remove(temp_script)
        print "Task %s M %d: delete temp_script %s" % (task_name, mapper_id, temp_script)
        print "Task %s M %d: Finished" % (task_name, mapper_id)
        self.master_c.mapper_finish(True, task_name, mapper_id, self.worker_ip_port)

    # read the data from split and also keep unit ( i.e. get next line from next split)
    # split_info : single file [(file_name, start, end)]
    # or multiple files [(file_name0, start, end), (file_name1, start, end)]
    # mapper_id : id of this mapper
    # num_mapper : how many reducers is in this task
    # file_info : all files info in this task
    # [(file0_path, file0_size), (file1_path, file1_size)]
    # split_size : the length of split data
    def read_input(self, split_info, mapper_id, num_mapper, file_info, split_size):
        data = ""
        filename = ""
        start = 0
        read_size = 0

        if debug:
            print "From worker mapper %d split_info" % mapper_id, split_info

        # read data from the split_info for this mapper
        for file in split_info:
            filename = file[0]
            start = file[1]
            read_size = file[2] - file[1]
            data += self.read_data_from_file(filename, start, read_size)
        last_file_path = filename
        start = start + read_size

        # get the last filename of this mapper in file_info
        used_file = 0
        for file in file_info:
            if file[0] == last_file_path:
                break
            used_file += 1

        if used_file > len(file_info):
            raise Exception("can't find the last file in split")

        # remove the data fetch by the previous mapper and get more data to keep unit
        # If unit is int, then data == the multiple of unit
        if type(self.unit) == int:
            # Remove the unit data fetched by the previous mapper if this mapper is not the first
            remove_size = 0
            for i in range(mapper_id):
                current_size = split_size - remove_size
                if (current_size % self.unit) == 0:
                    remaining_size = 0
                else:
                    remaining_size = self.unit - (current_size % self.unit)
                remove_size = remaining_size

            data = data[remove_size:]

            current_size = split_size - remove_size
            if (current_size % self.unit) == 0:
                remaining_size = 0
            else:
                remaining_size = self.unit - (current_size % self.unit)
            if debug:
                print "remove_size %d" % remove_size
                print "start %d remaining_size %d current_size %d" % (start, remaining_size, current_size)
                print "data ", data
            # Get more data ( if not the last mapper )
            if mapper_id != num_mapper - 1:
                while remaining_size > 0:
                    current_file_name = file_info[used_file][0]
                    current_file_size = file_info[used_file][1]
                    # Required remaining_size <= file remaining_size
                    if remaining_size <= (current_file_size - start):
                        data += self.read_data_from_file(current_file_name, start, remaining_size)
                        if remaining_size == current_file_size - start:
                            start = 0
                            used_file += 1
                        else:
                            start = start + remaining_size
                        remaining_size = 0
                    # Required remaining_size > file remaining_size
                    else:
                        if used_file < len(file_info) - 1:
                            data += self.read_data_from_file(current_file_name, start, current_file_size - start)
                            remaining_size -= current_file_size - start
                            start = 0
                            used_file += 1

                        # This is the last file, then finish split
                        else:
                            data += self.read_data_from_file(current_file_name, start, current_file_size - start)
                            remaining_size = 0

        # If unit is str, then get more data until we see the unit
        elif type(self.unit) == str:
            # Remove the first split if mapper_id is not 0
            if mapper_id != 0:
                if len(data.split(self.unit)) > 1:
                    data = data.split(self.unit, 1)[1]
                else:
                    data = ""
            # Get more split if the mapper is not the last mapper
            if mapper_id != num_mapper - 1:
                data += self.read_data_from_file(file_info[used_file][0], start, file_info[used_file][1] - start) \
                    .split(self.unit, 1)[0]
        if debug:
            print "From worker mapper %d input data" % mapper_id, data
        return data

    # read data from file
    def read_data_from_file(self, filename, start, read_size):
        f = open(filename)
        f.seek(start)
        data = f.read(read_size)
        try:
            f.close()
        except:
            print "Error: can't close the original data file"
        return data

    # read map_task from task_file,
    # task_file : (task_filename, code)
    # mapper_id : this mapper #
    # num_mapper : how many mappers is in this task
    # num_reducer : how many reducers is in this task
    # task_name : task name( it's the time that the client submit the job)
    # split_size : how many bytes is in one split
    def read_map_task(self, task_file, mapper_id, num_mapper, num_reducer, task_name, split_size):
        f = open(task_file[0] + 'm' + str(mapper_id), 'w')
        f.write(task_file[1])
        try:
            f.close()
        except:
            print "Error: can't close the map script"
        module = importlib.import_module(task_file[0][:-3])
        self.unit = module.unit
        self.map_object = module.Map(mapper_id, num_mapper, num_reducer, task_name, split_size)

    # reducer_id want to fetch intermediate file(mapper_id) from this mapper
    # but this current mapper_id can be different from mapper_id
    def fetch_intermediate_file(self, task_name, mapper_id, reducer_id):
        table = pickle.load(open(task_name + "_m" + str(mapper_id) + "_r" + str(reducer_id), 'rb'))
        return table

    def remove_intermediate_file(self, task_name, mapper_id, num_reducer):
        for reducer_id in range(num_reducer):
            intermediate_filename = task_name + '_m' + str(mapper_id) + "_r" + str(reducer_id)
            os.remove(intermediate_filename)

    # master want to fetch output file from this reducer
    def fetch_result_file(self, output_file, reducer_id):
        output_filename = output_file + '_' + str(reducer_id)
        f = open(output_filename, 'rb')
        result = f.read()
        try:
            f.close()
        except:
            print "Error: can't close intermediate file"
        os.remove(output_filename)
        return result

    # Methods for reducer
    def do_reduce(self, task_file, num_mapper, reducer_id, output_file, task_name):
        gevent.spawn(self.do_reduce_async, task_file, num_mapper, reducer_id, output_file, task_name)

    def do_reduce_async(self,task_file, num_mapper, reducer_id, output_file, task_name):
        print "Get task %s from master, current reducer id is %d" % (task_name, reducer_id)
        self.read_reduce_task(task_file, reducer_id, output_file)
        print "Task %s R %d: read user reduce function successfully" % (task_name, reducer_id)

        entire_data = {}
        while len(entire_data) != num_mapper:
            # Ensure reducer get master ping()
            # gevent.sleep(0)

            try:
                mapper_id, mapper_ip_port = self.reducer_map_queue.get()
            except:
                print "**** Error: can't get enough mapper intermediate file"
                return
            if mapper_ip_port == self.worker_ip_port:
                try:
                    data = pickle.load(open(task_name + "_m" + str(mapper_id) +
                                            '_r' + str(reducer_id), 'rb'))
                except:
                    print "**** Error: can't get intermediate file from local mapper %d %s" \
                          % (mapper_id, mapper_ip_port)
            else:
                try:
                    c = zerorpc.Client()
                    c.connect("tcp://" + mapper_ip_port)
                    data = c.fetch_intermediate_file(task_name, mapper_id, reducer_id)
                    c.close()
                except:
                    print "**** Error: can't get intermediate file from mapper %d %s" \
                          % (mapper_id, mapper_ip_port)
            entire_data[mapper_id] = data
            print "Task %s R %d: get intermediate data from mapper %d: %s" \
                  % (task_name, reducer_id, mapper_id, mapper_ip_port)
        self.reduce_object.reduce(task_name, entire_data)
        print "Task %s R %d: do reduce successfully" % (task_name, reducer_id)
        self.reduce_object.write_to_file()
        print "Task %s R %d: write result to file successfully" % (task_name, reducer_id)
        temp_script = task_file[0] + "r" + str(reducer_id)
        os.remove(temp_script)
        print "Task %s R %d: delete temp_script %s" % (task_name, reducer_id, temp_script)
        print "Task %s R %d: Finished" % (task_name, reducer_id)
        self.master_c.reducer_finish(True, task_name, reducer_id, self.worker_ip_port)

    # reducer is notified by master that the mapper has finished its job
    def notify_mapper_finish(self, mapper_id, mapper_ip_port):
        self.reducer_map_queue.put_nowait((mapper_id, mapper_ip_port))

    # Read reduce_task from task_file
    # task_file = (task_filename, code)
    def read_reduce_task(self, task_file, reduce_id, output_file):
        f = open(task_file[0] + 'r' + str(reduce_id), 'w')
        f.write(task_file[1])
        try:
            f.close()
        except:
            print "Error: can't close the reduce script"
        module = importlib.import_module(task_file[0][:-3])
        self.reduce_object = module.Reduce(reduce_id, output_file)


if __name__ == "__main__":
    master_ip_port = sys.argv[1]
    ip = sys.argv[2][:-5]
    port = sys.argv[2][-4:]
    worker_ip_port = sys.argv[2]
    logging.basicConfig()
    worker = Worker(master_ip_port, worker_ip_port)
    s = zerorpc.Server(worker)
    s.bind('tcp://' + worker_ip_port)
    c = zerorpc.Client()
    c.connect('tcp://' + master_ip_port)
    worker.master_c = c
    worker.data_dir = c.register(worker_ip_port)
    s.run()






