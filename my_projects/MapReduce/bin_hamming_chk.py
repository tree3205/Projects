from __future__ import division
import math
import cPickle as pickle
import gevent

unit = 3
debug = False


class Map(object):

    def __init__(self, mapper_id, num_mapper, num_reducer, task_name, split_size):
        self.table = {}
        self.mapper_id = mapper_id
        self.task_name = task_name
        self.num_mapper = num_mapper
        self.num_reducer = num_reducer
        self.split_size = split_size
        for i in range(self.num_reducer):
            self.table[i] = []

    def map(self, k, v):
        key = self.partition(self.mapper_id, self.num_mapper)

        temp_string = ''

        for char in v:
            binary_str = "{0:08b}".format(ord(char))
            temp_string += binary_str

        data_num = len(temp_string) - (len(temp_string) % 12)
        text = temp_string[:data_num]
        bad_list = []
        byte_pos = 0
        for i in range(int(len(text)/12)):
            # Ensure mapper get master ping()
            gevent.sleep(0)
            byte_str = text[i*12: (i+1)*12]
            bad_flag, bad_pos = self.check(byte_str)
            if bad_flag:
                if debug:
                    print "bad_pos, byte_pos, mapper_id, split_size"
                    print bad_pos, byte_pos * 12, self.mapper_id, self.split_size
                bad_list.append(bad_pos + byte_pos * 12 + self.mapper_id * self.split_size * 8)
            byte_pos += 1
        self.emit(key, bad_list)

    def combine(self):
        pass

    def emit(self, key, bad_list):
        self.table[key] += bad_list

    def partition(self, mapper_id, num_mapper):
        # using precious division
        return int(math.floor(mapper_id / (num_mapper / self.num_reducer)))

    # Write the whole dictionary into intermediate file
    def write_to_file(self):
        if debug:
            print "map %d output data" % self.mapper_id, self.table
        for i in range(self.num_reducer):
            pickle.dump(self.table[i], open(self.task_name+'_m'+str(self.mapper_id)+"_r"+str(i), "wb"))

    def check(self, e):
        """
        Check a string(12 chars) using hamming code.
        :param e: 12 bits encoded text      e.g. '011100101010'
        :return:  bad_flag: true if this 12bits have error
                  bad_pos:  the error position (start at 0)
        """
        check_list = [[0, 2, 4, 6, 8, 10],
                        [1, 2, 5, 6, 9, 10],
                        [3, 4, 5, 6, 11],
                        [7, 8, 9, 10, 11]]
        bad_flag = False
        bad_pos = 0
        for index, l in enumerate(check_list):
            sum = 0
            for i in l:
                sum += int(e[i])
            if sum % 2 == 1:
                bad_flag = True
                bad_pos += pow(2, index)
        # Position starts at 0
        return bad_flag, bad_pos-1


class Reduce(object):
    def __init__(self, reducer_id, output_file):
        self.result = []
        self.reducer_id = reducer_id
        self.output_file = output_file

    def reduce(self, k, vdict):
        keys = vdict.keys()
        keys.sort()

        for mapper_id in keys:
            for pos in vdict[mapper_id]:
                # gevent.sleep(0)
                self.result.append(pos)

    def write_to_file(self):
        f = open(self.output_file+"_"+str(self.reducer_id), "wb")
        for err_pos in self.result:
            f.write("Error position: %s" % err_pos + '\n')
        f.close()



