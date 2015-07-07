import sys
import time
from datetime import datetime

import zerorpc
import gevent




# Usage:
# python mr_job.py <ip_address_master:port>[<name> | <mr_class.py>]  <split_size> <num_reducers>
# [<input_filename> | <base_filename>_] <output_filename_base>
#
# For example: python mr_job.py 0.0.0.0:3205 wordcount 100000 4 book.txt count
# or python mr_job.py 0.0.0.0:3205 word_count.py 100000 4 book.txt count
# or python mr_job.py 0.0.0.0:3205 word_count.py 3000 4 README.md count


class Job(object):
    def __init__(self):
        self.task_name = ""
        self.master_info = None

    def assign_job(self, master_ip_address, task_file, split_size, num_reducer, input_file, output_file):
        start_time = datetime.now()
        c = zerorpc.Client()
        c.connect(master_ip_address)
        self.master_info = (master_ip_address, c)
        self.task_name = c.do_job(task_file, split_size, num_reducer, input_file, output_file)
        print "Initialize task: task name is %s" % self.task_name
        job_done = False

        while not job_done:
            print
            print "###### job_tracker : task name:%s ######" % self.task_name
            print time.strftime("%m/%d/%Y %H:%M:%S", time.localtime())
            job_tracker = c.client_query(self.task_name)

            finished_mapper = job_tracker['finished_mapper']
            assigned_mapper = job_tracker['assigned_mapper']
            needed_mapper = job_tracker['needed_mapper']
            finished_reducer = job_tracker['finished_reducer']
            assigned_reducer = job_tracker['assigned_reducer']
            needed_reducer = job_tracker['needed_reducer']

            print "Mappers finish/assign/need : %d / %d / %d" % (finished_mapper,
                                                                       assigned_mapper, needed_mapper)
            print "Reducers finish/assign/need : %d / %d / %d" % (finished_reducer,
                                                                        assigned_reducer, needed_reducer)
            if finished_mapper == needed_mapper and finished_reducer == needed_reducer:
                job_done = True
                print "Job finished"
                end_time = datetime.now()
                print('Duration: {}'.format(end_time - start_time))
            gevent.sleep(1)


if __name__ == "__main__":
    job = Job()
    master_ip_address = 'tcp://' + sys.argv[1]
    task_filename = sys.argv[2]
    split_size = sys.argv[3]
    num_reducer = sys.argv[4]
    input_file = sys.argv[5]
    output_file = sys.argv[6]

    f = open(task_filename)
    code = f.read()
    task_file = (task_filename, code)
    job.assign_job(master_ip_address, task_file, split_size, num_reducer, input_file, output_file)


