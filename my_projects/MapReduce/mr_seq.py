# Usage: the job and input are in current work directory as default
# For example:
# python mr_seq.py word_count.py 3000 3 word_count_test.txt count_all => For word count
# python mr_seq.py bin_hamming_enc.py 1000 2 test_data bin_enc => For binary hamming encode


import fnmatch
import os
import sys
import math
import time
import logging
from datetime import datetime
import importlib
import cPickle as pickle
import gevent

debug = False

class SequentialWork(object):
    def __init__(self):
        self.jobs_tracker = {}
        self.task = ''
        self.data_dir = os.getcwd()

    # Master gets job from client, split input data according to the split size.
    # Then it assigns jobs to mappers and reducers.
    def do_job(self, task_file, split_size, num_reducer, input_file, output_file):
        self.output_file = output_file
        # identical name for each task
        # use this name to generate intermediate filename
        task_name = time.strftime("%Y%m%d%H%M%S", time.localtime())
        self.task_name = task_name
        print "Task %s get" % task_file[0]
        start_time = datetime.now()
        split_size = int(split_size)
        num_reducer = int(num_reducer)
        split_infos, file_info = self.split_file(split_size, input_file)

        num_mapper = len(split_infos)
        # initialize jobs_tracker for task_name
        self.jobs_tracker[task_name] = {"mappers": {}, "reducers": {},
                                        "num_mapper": num_mapper, "num_reducer": num_reducer,
                                        "task_file": task_file, "split_infos": split_infos,
                                        "file_info": file_info, "output_file": output_file,
                                        "split_size": split_size, "done": False}

        print "Task " + task_name + " : assigning %d mappers, %d reducers" % (num_mapper, num_reducer)
        # Map task
        mapper_procs = gevent.spawn(self.assign_mappers, task_name)
        mapper_procs.join()
        # Reduce task
        reducer_procs = gevent.spawn(self.assign_reducers, task_name)
        reducer_procs.join()
        print "Task finished"
        end_time = datetime.now()
        print('Duration: {}'.format(end_time - start_time))

    # Split the input file and store the associated information
    # in a list which contains dictionary. Each dictionary is a Mapper's input.
    def split_file(self, split_size, input_file):
        """ One split only has one file.
            split_info = {0:[(file_name0, start, end)], 1:[(file_name1, start, end)]}
            One split may has more than one file.
            split_info =  {0:[(file_name0, start, end), (file_name1, start, end)],
                           1:[(file_name1, start, end)]}

            file_info = [(file0_path, file0_size), (file1_path, file1_size)]
        """
        split_info = {}
        file_info = []
        # Single file
        if not input_file.endswith('_'):
            file_path = self.data_dir + '/' + input_file
            file_size = os.path.getsize(file_path)
            split_num = int(math.ceil(float(file_size) / split_size))
            # Split file
            for i in range(split_num):
                split_info[i] = []
                start = i * split_size
                if (start + split_size) > file_size:
                    end = file_size
                else:
                    end = start + split_size
                split_info[i].append((file_path, start, end))
            file_info = [(file_path, file_size)]
        # Multiple files
        else:
            # Get all file name by the base name
            # and calculate the total file size.
            # file_info = [[file_dir1, file_size], [file_dir2, file_size], ...]
            total_size = 0
            for root, dir_names, file_names in os.walk(self.data_dir):
                for file_name in fnmatch.filter(file_names, input_file + '*'):
                    dir_file = root + '/' + file_name
                    one_file_size = os.path.getsize(dir_file)
                    total_size += one_file_size
                    file_info.append((dir_file, one_file_size))

            # Get worker num(split num)
            split_num = int(math.ceil(float(total_size) / split_size))

            # Split file
            start = 0
            used_file = 0
            for i in range(split_num):
                remaining_size = split_size
                split_info[i] = []
                while remaining_size > 0:
                    current_file_name = file_info[used_file][0]
                    current_file_size = file_info[used_file][1]
                    # Required remaining_size <= file remaining_size
                    if remaining_size <= (current_file_size - start):
                        split_info[i].append((current_file_name, start, start + remaining_size))
                        if remaining_size == current_file_size - start:
                            start = 0
                            used_file += 1
                        else:
                            start = start + remaining_size
                        remaining_size = 0
                    # Required remaining_size > file remaining_size
                    else:
                        if used_file < len(file_info) - 1:
                            split_info[i].append((current_file_name, start, current_file_size))
                            remaining_size -= current_file_size - start
                            start = 0
                            used_file += 1

                        # This is the last file, then finish split
                        else:
                            split_info[i].append((current_file_name, start, current_file_size))
                            remaining_size = 0
        return split_info, file_info

    # Assign map jobs to free mappers
    def assign_mappers(self, task_name):
        num_mapper = self.jobs_tracker[task_name]["num_mapper"]
        procs = []
        for mapper_id in range(num_mapper):
            print "Task " + task_name + " : mappers id %d assigned" % mapper_id
            proc = gevent.spawn(self.assign_mapper, mapper_id, task_name)
            proc.join()

            # gevent.joinall(procs)

    # Assign map job to a single free mapper
    # After the mapper finished its map job, return back to free mapper queue
    # and notify all reducers to fetch intermediate data
    def assign_mapper(self, mapper_id, task_name):
        job_dict = self.jobs_tracker[task_name]
        split_infos = job_dict["split_infos"]
        task_file = job_dict["task_file"]
        num_mapper = job_dict["num_mapper"]
        num_reducer = job_dict["num_reducer"]
        file_info = job_dict["file_info"]
        split_size = job_dict["split_size"]

        self.do_map(split_infos[mapper_id], task_file,
                    mapper_id, num_mapper, num_reducer, task_name, file_info, split_size)
        print "Task %s : mapper %d finished" % (task_name, mapper_id)


    # Assign reduce jobs to free reducers
    def assign_reducers(self, task_name):
        num_reducer = self.jobs_tracker[task_name]["num_reducer"]
        procs = []
        for i in range(num_reducer):
            print "Task " + task_name + " : reducer id %d assigned" % i
            proc = gevent.spawn(self.assign_reducer, i, task_name)
            proc.join()

    # Assign one reduce job to one reducer
    def assign_reducer(self, reducer_id, task_name):
        task_file = self.jobs_tracker[task_name]["task_file"]
        num_mapper = self.jobs_tracker[task_name]["num_mapper"]
        output_file = self.jobs_tracker[task_name]["output_file"]
        self.do_reduce(task_file, num_mapper, reducer_id, output_file, task_name)

    # Methods for mapper
    def do_map(self, split_info, task_file, mapper_id, num_mapper, num_reducer, task_name, file_info, split_size):
        print "Get task %s from master, current mapper id is %d" % (task_name, mapper_id)
        self.read_map_task(task_file, mapper_id, num_mapper, num_reducer, task_name, split_size)
        print "Task %s M %d: read user map function successfully" % (task_name, mapper_id)
        data = self.read_input(split_info, mapper_id, num_mapper, file_info, split_size)
        print "Task %s M %d: read input successfully" % (task_name, mapper_id)
        self.map_object.map(task_file[0], data)
        print "Task %s M %d: do map successfully" % (task_name, mapper_id)
        self.map_object.combine()
        print "Task %s M %d: combine intermediate result successfully" % (task_name, mapper_id)
        self.map_object.write_to_file()
        print "Task %s M %d: write result to file successfully" % (task_name, mapper_id)
        temp_script = task_file[0] + "m" + str(mapper_id)
        os.remove(temp_script)
        print "Task %s M %d: delete temp_script %s" % (task_name, mapper_id, temp_script)
        print "Task %s M %d: Finished" % (task_name, mapper_id)
        success = True
        return success

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
        f.close()
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
        f.close()
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
        f.close()
        os.remove(output_filename)
        return result

    # Methods for reducer
    def do_reduce(self, task_file, num_mapper, reducer_id, output_file, task_name):
        print "Get task %s from master, current reducer id is %d" % (task_name, reducer_id)
        self.read_reduce_task(task_file, reducer_id, output_file)
        print "Task %s R %d: read user reduce function successfully" % (task_name, reducer_id)

        entire_data = {}
        for mapper_id in range(num_mapper):
            try:
                data = pickle.load(open(task_name + "_m" + str(mapper_id) +
                                        '_r' + str(reducer_id), 'rb'))
            except:
                print "**** Error: can't get intermediate file from local mapper %d" \
                      % mapper_id

            entire_data[mapper_id] = data
            print "Task %s R %d: get intermediate data from mapper %d" \
                  % (task_name, reducer_id, mapper_id)
        self.reduce_object.reduce(task_name, entire_data)
        print "Task %s R %d: do reduce successfully" % (task_name, reducer_id)
        self.reduce_object.write_to_file()
        print "Task %s R %d: write result to file successfully" % (task_name, reducer_id)
        temp_script = task_file[0] + "r" + str(reducer_id)
        os.remove(temp_script)
        print "Task %s R %d: delete temp_script %s" % (task_name, reducer_id, temp_script)
        print "Task %s R %d: Finished" % (task_name, reducer_id)

    # Read reduce_task from task_file
    # task_file = (task_filename, code)
    def read_reduce_task(self, task_file, reduce_id, output_file):
        f = open(task_file[0] + 'r' + str(reduce_id), 'w')
        f.write(task_file[1])
        f.close()
        module = importlib.import_module(task_file[0][:-3])
        self.reduce_object = module.Reduce(reduce_id, output_file)

    # Collector get result from master
    def collect_result(self):
        print "Receive collect command: collect "
        task_name = self.task_name
        job_dict = self.jobs_tracker[task_name]
        for mapper_id in range(job_dict["num_mapper"]):
            try:
                self.remove_intermediate_file(task_name, mapper_id, job_dict["num_reducer"])
            except:
                print "**** Error: can't find file %s_%d_*" % (task_name, mapper_id)
        print "collect " + output_file + " from " + task_name
        job_dict = self.jobs_tracker[task_name]
        result = ""
        for reducer_id in range(job_dict["num_reducer"]):
            result += self.fetch_result_file(output_file, reducer_id)
        f = open(self.output_file, 'wb')
        f.write(result)
        f.close()


if __name__ == "__main__":
    logging.basicConfig()
    seq = SequentialWork()
    task_filename = sys.argv[1]
    split_size = sys.argv[2]
    num_reducer = sys.argv[3]
    input_file = sys.argv[4]
    output_file = sys.argv[5]

    f = open(task_filename)
    code = f.read()
    task_file = (task_filename, code)
    seq.do_job(task_file, split_size, num_reducer, input_file, output_file)
    seq.collect_result()







