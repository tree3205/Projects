__author__ = 'WofloW'
# python wc_test.py README.md count_0

import sys
import string
import time
from datetime import datetime

class wc_test(object):

    def __init__(self):
        self.table = {}

    def compare(self, original_text, result):
        start_time = datetime.now()
        f = open(original_text, 'r')
        data = f.read()
        data = data.replace('\n', ' ').replace('\r', ' ')
        words = data.split()

        for word in words:
            word = word.strip(string.punctuation)
            if word != '':
                if word in self.table:
                    self.table[word] += 1
                else:
                    self.table[word] = 1
        end_time = datetime.now()
        print('In memory duration: {}'.format(end_time - start_time))
        # print self.table
        ori_result = ""

        # keys = self.table.keys()
        # keys.sort()

        # for word in keys:
        #     ori_result += word + ': ' + str(self.table[word]) + "\n"

        # ori_result_filename = "ori_result.txt"
        # f0 = open(ori_result_filename, 'wb')
        # f0.write(ori_result)
        # f0.close()

        f1 = open(result, 'rb')
        line = f1.readline()
        result_table = {}
        while len(line):
            data = line.split(": ")
            result_table[data[0]] = int(data[1].strip('\n'))
            line = f1.readline()
        f1.close()

        shared_items = set(self.table.items()) & set(result_table.items())
        # print set(self.table.items())
        # print set(result_table.items())

        if len(shared_items) == len(self.table.items()) and len(shared_items) == len(result_table.items()):
            print 'Final result: same\n'
        else:
            print 'Final result: different\n'



if __name__ == "__main__":
    original_text = sys.argv[1]
    map_reduce_result = sys.argv[2]
    w = wc_test()
    print time.strftime("%m/%d/%Y %H:%M:%S", time.localtime())
    w.compare(original_text, map_reduce_result)
