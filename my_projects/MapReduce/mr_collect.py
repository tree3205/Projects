__author__ = 'treexy1230'

import sys

import zerorpc


# Usage:
# python mr_collect.py <filename_base> <output_filename>
# For example: $ python mr_collect.py count count_all


class Collect(object):
    def __init__(self, filename_base, output_filename, master_ip_port):
        self.filename_base = filename_base
        self.output_filename = output_filename
        c = zerorpc.Client()
        c.connect("tcp://" + master_ip_port)
        self.master_info = (master_ip_port, c)

    def get_result(self):
        success, output = self.master_info[1].get_result(self.filename_base)
        if success:
            f = open(self.output_filename, 'wb')
            f.write(output)
            f.close()
            print "Collect job finished"
        else:
            print "Can't find a job with output: " + filename_base


if __name__ == "__main__":
    filename_base = sys.argv[1]
    output_filename = sys.argv[2]
    master_ip_port = sys.argv[3]
    collect = Collect(filename_base, output_filename, master_ip_port)
    collect.get_result()