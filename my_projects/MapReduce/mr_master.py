# Usage:
# python mr_worker.py port
# For example: python mr_master.py 3205 data_dir

import fnmatch
import os
import sys
import math
import time
import logging

import zerorpc
import gevent
from gevent.queue import Queue
from gevent.lock import BoundedSemaphore


STATE_READY = 'READY'
STATE_BUSY = 'BUSY'


class Master(object):
    """ workers: a dictionary to store the information about workers
        format: {ip_port: ('Status', Remote_call)}

        jobs_tracker: a dictionary to store the information about jobs
        format:
        { task_name:
              { "mappers":
                      { mapper_id: [remote_call, mapper_ip_port, split_info, finished]},
                "reducers":
                      { reducer_id: [remote_call, reducer_ip_port, finished]},
                "num_mapper":
                      num_mapper,
                "num_reducer":
                      num_reducer,
                "task_file":
                      [filename, codes],
                "split_infos":
                      split_infos,
                "file_info":
                      file_info,
                "output_file:
                      output_file
                "done":
                      True/False
                 }
        }

        mapper_queue: free mapper queue
        format: [(ip_port, remote_call)]

        reducer_queue: free reducer queue
        format: [(ip_port, remote_call)]
    """

    def __init__(self, port, data_dir):
        gevent.spawn(self.controller)
        self.state = STATE_READY
        self.workers = {}
        self.jobs_tracker = {}
        self.port = port
        self.data_dir = data_dir
        self.mapper_queue = Queue()
        self.reducer_queue = Queue()
        self.jobs_tracker_lock = BoundedSemaphore(1)
        self.workers_lock = BoundedSemaphore(1)

    def controller(self):
        while True:
            print '[Master:%s] ' % self.state
            # down_workers = []
            # self.workers_lock.acquire()
            local_workers = dict(self.workers)
            for w in local_workers:
                print '(%s, %s)' % (w, local_workers[w][0])
                # not spawn a coroutine to ping this worker
                if local_workers[w][2] == False:
                    local_workers[w][2] = True
                    gevent.spawn(self.each_ping, w, local_workers[w][1])
            gevent.sleep(1)

    def each_ping(self, ip_port, c):
        alive = True
        while alive:
            try:
                c.ping()
            except:
                print "**** Error: Worker %s is down" % ip_port
                self.workers.pop(ip_port)
                print "********** Reassign jobs in %s" % ip_port
                gevent.spawn(self.reassign_job, [ip_port])
                alive = False
            gevent.sleep(1)

    # Failure tolerance
    # reassign the failure worker's job to another worker
    # remote = (ip_port, c)
    # def assign_mapper(self, split_info, task_file,
    #                  mapper_id, num_mapper, num_reducer, task_name, file_info):
    # def assign_reducer(self, task_file, num_mapper, reducer_id, output_file, task_name):
    def reassign_job(self, down_workers):
        self.jobs_tracker_lock.acquire()
        reassign_list = []
        for down_worker in down_workers:
            for task_name in self.jobs_tracker:
                # whether deal with failure after the job is done
                # if self.jobs_tracker[task_name]["done"] == False:
                job_dict = self.jobs_tracker[task_name]
                for mapper_id in job_dict["mappers"]:
                    if job_dict["mappers"][mapper_id][1] == down_worker:
                        print "********** down %s did %s mapper %d" % (down_worker, task_name, mapper_id)
                        job_dict["mappers"][mapper_id][3] = False
                        reassign_list.append([task_name, mapper_id, 0])
                for reducer_id in job_dict["reducers"]:
                    if job_dict["reducers"][reducer_id][1] == down_worker:
                        print "********** down %s did %s reducer %d" % (down_worker, task_name, reducer_id)
                        job_dict["reducers"][reducer_id][2] = False
                        reassign_list.append([task_name, reducer_id, 1])
        self.jobs_tracker_lock.release()
        for reassign in reassign_list:
            task_name = reassign[0]
            # Reassign mapper
            if reassign[2] == 0:
                mapper_id = reassign[1]
                print "********** Reassign %s mapper %d" % (task_name, mapper_id)
                gevent.spawn(self.reassign_mapper, mapper_id, task_name)
            # Reassign reducer
            elif reassign[2] == 1:
                reducer_id = reassign[1]
                self.jobs_tracker[task_name]["reducers"].pop(reducer_id)
                print "********** Reassign %s reducer %d" % (task_name, reducer_id)
                gevent.spawn(self.reassign_reducer, reducer_id, task_name)

    def register(self, ip_port):
        gevent.spawn(self.register_async, ip_port)
        return self.data_dir

    def register_async(self, ip_port):
        print '[Master:%s] ' % self.state,
        print 'Registered worker (%s)' % ip_port
        c = zerorpc.Client()
        c.connect("tcp://" + ip_port)
        # self.workers_lock.acquire()
        self.workers[ip_port] = [STATE_READY, c, False]
        # self.workers_lock.release()
        self.mapper_queue.put_nowait((ip_port, c))
        self.reducer_queue.put_nowait((ip_port, c))
        c.ping()

    # Master gets job from client, split input data according to the split size.
    # Then it assigns jobs to mappers and reducers.
    def do_job(self, task_file, split_size, num_reducer, input_file, output_file):
        # identical name for each task
        # use this name to generate intermediate filename
        task_name = time.strftime("%Y%m%d%H%M%S", time.localtime())
        gevent.spawn(self.do_job_async, task_file, split_size, num_reducer, input_file, output_file, task_name)
        return task_name

    # create a new coroutine to handle client's job
    def do_job_async(self, task_file, split_size, num_reducer, input_file, output_file, task_name):
        print "Task %s get" % task_file[0]
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
        gevent.spawn(self.assign_mappers, task_name)
        # Reduce task
        gevent.spawn(self.assign_reducers, task_name)

    # Client gets report from master
    def client_query(self, task_name):
        # print "client_query for %s" % task_name
        job_tracker = self.jobs_tracker[task_name]
        mappers = job_tracker["mappers"]
        reducers = job_tracker["reducers"]
        needed_mapper = job_tracker["num_mapper"]
        needed_reducer = job_tracker["num_reducer"]

        finished_mapper_num = 0
        finished_reducer_num = 0
        for mapper in mappers:
            if mappers[mapper][3]:
                finished_mapper_num += 1

        for reducer in reducers:
            if reducers[reducer][2]:
                finished_reducer_num += 1

        result_dict = {"finished_mapper": finished_mapper_num, "assigned_mapper": len(mappers),
                       "needed_mapper": needed_mapper, "finished_reducer": finished_reducer_num,
                       "assigned_reducer": len(reducers), "needed_reducer": needed_reducer}
        if finished_reducer_num == needed_reducer:
            job_dict = self.jobs_tracker[task_name]
            print 'Task %s finished ' % task_name
            self.jobs_tracker[task_name]["done"] = True
        return result_dict

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
        for mapper_id in range(num_mapper):
            ip_port, c = self.mapper_queue.get()
            # Handle failure before assign task.
            while ip_port not in self.workers:
                ip_port, c = self.mapper_queue.get()
            print "Task " + task_name + " : mappers id %d assigned to %s" % (mapper_id, ip_port)
            gevent.spawn(self.assign_mapper, ip_port, c, mapper_id, task_name)

    # Assign map job to a single free mapper
    # After the mapper finished its map job, return back to free mapper queue
    # and notify all reducers to fetch intermediate data
    def assign_mapper(self, ip_port, c, mapper_id, task_name):
        job_dict = self.jobs_tracker[task_name]
        split_info = job_dict["split_infos"][mapper_id]
        task_file = job_dict["task_file"]
        num_mapper = job_dict["num_mapper"]
        num_reducer = job_dict["num_reducer"]
        file_info = job_dict["file_info"]
        split_size = job_dict["split_size"]

        self.jobs_tracker_lock.acquire()
        self.jobs_tracker[task_name]["mappers"][mapper_id] = [c, ip_port, split_info, False]
        self.jobs_tracker_lock.release()
        try:
            success = c.do_map(split_info, task_file,
                               mapper_id, num_mapper, num_reducer, task_name, file_info, split_size)
        except:
            print "**** Error: Can't assign task %s map task to mapper %d %s" \
                  % (task_name, mapper_id, ip_port)

    def mapper_finish(self, success, task_name, mapper_id, ip_port):
        if success:
            """ jobs_tracker =
             { task_name:
              { "mappers":
                      { mapper_id: [remote_call, mapper_ip_port, split_info, finished]}
            """
            self.jobs_tracker[task_name]["mappers"][mapper_id][3] = True
            print "Task %s : mapper %d finished" % (task_name, mapper_id)

            self.jobs_tracker_lock.acquire()
            reducers_dict = self.jobs_tracker[task_name]["reducers"]
            for reducer_id in reducers_dict:
                reducer_c = reducers_dict[reducer_id][0]
                print "mapper %d is notifying reducer %d" % (mapper_id, reducer_id)
                try:
                    reducer_c.notify_mapper_finish(mapper_id, ip_port)
                except:
                    print "**** Error: Task %s mapper %d can't notify reducer %d %s" \
                          % (task_name, mapper_id, reducer_id, reducers_dict[reducer_id][1])
                print "Mapper %d is notifying reducer %d done" % (mapper_id, reducer_id)
            self.jobs_tracker_lock.release()
        else:
            print "Task %s : mapper %d failed" % (task_name, mapper_id)
        if ip_port in self.workers:
            print "%s returns to free mapper queue." % ip_port
            self.mapper_queue.put_nowait((ip_port, self.workers[ip_port][1]))

    # Assign reduce jobs to free reducers
    def assign_reducers(self, task_name):
        num_reducer = self.jobs_tracker[task_name]["num_reducer"]
        procs = []
        for i in range(num_reducer):
            ip_port, c = self.reducer_queue.get()
            while ip_port not in self.workers:
                ip_port, c = self.reducer_queue.get()
            print "Task " + task_name + " : reducer id %d assigned to %s" % (i, ip_port)
            proc = gevent.spawn(self.assign_reducer, ip_port, c, i, task_name)
            procs.append(proc)

    # Assign one reduce job to one reducer
    def assign_reducer(self, ip_port, c, reducer_id, task_name):
        task_file = self.jobs_tracker[task_name]["task_file"]
        num_mapper = self.jobs_tracker[task_name]["num_mapper"]
        output_file = self.jobs_tracker[task_name]["output_file"]

        self.jobs_tracker_lock.acquire()
        self.jobs_tracker[task_name]["reducers"][reducer_id] = [c, ip_port, False]
        for mapper_id in self.jobs_tracker[task_name]["mappers"]:
            if self.jobs_tracker[task_name]["mappers"][mapper_id][3]:
                c.notify_mapper_finish(mapper_id, self.jobs_tracker[task_name]["mappers"][mapper_id][1])
        self.jobs_tracker_lock.release()
        try:
            c.do_reduce(task_file, num_mapper, reducer_id, output_file, task_name)
        except:
            print "**** Error: Can't assign task %s reduce task to reducer %d %s" \
                  % (task_name, reducer_id, ip_port)

    def reducer_finish(self, success, task_name, reducer_id, ip_port):
        if success:
            """ jobs_tracker =
                { task_name:
                  { "reducers":
                          { reducer_id: [remote_call, reducer_ip_port, finished]}
            """
            self.jobs_tracker[task_name]["reducers"][reducer_id][2] = True
            print "Task %s : reducer %d finished" % (task_name, reducer_id)
        else:
            print "Task %s : reducer %d failed" % (task_name, reducer_id)
        if ip_port in self.workers:
            self.reducer_queue.put_nowait((ip_port, self.workers[ip_port][1]))
            print "%s returns to free reducer queue." % ip_port

    # Reassign one map job to one mapper
    def reassign_mapper(self, mapper_id, task_name):
        ip_port, c = self.mapper_queue.get()
        while ip_port not in self.workers:
            ip_port, c = self.mapper_queue.get()
        print "Reassign Task %s : mappers id %d to %s" % (task_name, mapper_id, ip_port)

        self.assign_mapper(ip_port, c, mapper_id, task_name)

    # Reassign one reduce job to one reducer
    def reassign_reducer(self, reducer_id, task_name):
        job_dict = self.jobs_tracker[task_name]
        ip_port, c = self.reducer_queue.get()
        while ip_port not in self.workers:
            ip_port, c = self.reducer_queue.get()
        print "Reassign Task %s : reducer id %d to %s" % (task_name, reducer_id, ip_port)
        self.assign_reducer(ip_port, c, reducer_id, task_name)

    # Collector get result from master
    def get_result(self, filename_base):
        print "Receive collect command: collect " + filename_base
        keys = self.jobs_tracker.keys()
        keys.sort(reverse=True)
        for task_name in keys:
            if self.jobs_tracker[task_name]["output_file"] == filename_base:

                job_dict = self.jobs_tracker[task_name]
                for mapper_id in job_dict["mappers"]:
                    try:
                        job_dict["mappers"][mapper_id][0]\
                            .remove_intermediate_file(task_name, mapper_id, job_dict["num_reducer"])
                    except:
                        print "**** Error: task %s: mapper %d lost connection" % (task_name, mapper_id)
                print "collect " + filename_base + " from " + task_name
                job_dict = self.jobs_tracker[task_name]
                result = ""
                for reducer_id in job_dict["reducers"]:
                    result += job_dict["reducers"][reducer_id][0]\
                        .fetch_result_file(filename_base, reducer_id)
                self.jobs_tracker.pop(task_name, None)
                return True, result
        print "Error: Can't find a job with output: " + filename_base
        return False, ''


if __name__ == "__main__":
    port = sys.argv[1]
    data_dir = sys.argv[2]
    logging.basicConfig()
    s = zerorpc.Server(Master(port, data_dir))
    s.bind('tcp://0.0.0.0:' + port)
    s.run()
