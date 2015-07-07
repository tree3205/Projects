from __future__ import division
import math
import cPickle as pickle
import gevent

unit = 1
debug = False
err_pos_list = [30]


class Map(object):

    def __init__(self, mapper_id, num_mapper, num_reducer, task_name, split_size):
        self.table = {}
        self.mapper_id = mapper_id
        self.task_name = task_name
        self.num_mapper = num_mapper
        self.num_reducer = num_reducer
        self.split_size = split_size
        for i in range(self.num_reducer):
            self.table[i] = ""

    def map(self, k, v):
        key = self.partition(self.mapper_id, self.num_mapper)

        text = self.to_bin(v)
        bad_list = []
        for err_pos in err_pos_list:
            gevent.sleep(0)
            # using precious division
            err_mapper_id = math.floor(err_pos / (self.split_size * 8))
            err_pos_in_mapper = err_pos % (self.split_size * 8)
            if err_mapper_id == self.mapper_id:
                text = self.flip(err_pos_in_mapper, text)
        self.emit(key, text)

    def combine(self):
        pass

    def emit(self, key, err_string):
        self.table[key] = err_string

    def partition(self, mapper_id, num_mapper):
        # using precious division
        return int(math.floor(mapper_id / (num_mapper / self.num_reducer)))

    # Write the whole dictionary into intermediate file
    def write_to_file(self):
        if debug:
            print "map %d output data" % self.mapper_id, self.table
        for i in range(self.num_reducer):
            pickle.dump(self.table[i], open(self.task_name+'_m'+str(self.mapper_id)+"_r"+str(i), "wb"))

    def to_bin(self, text):
        """
        Convert a character string to a binary string(1 byte to 8 bytes)
        :param text:  character             e.g. 'ab'
        :return:      binary string         e.g. '0110000101100010'
        """
        out_string = ''
        for c in text:
            out_string += "{0:08b}".format(ord(c))
        return out_string

    def flip(self, position, code):
        """
        Flip the binary code in the position of code.
        :param position: the position you want to flip    e.g. flip(1,'110') = '100'
        :param code: input binary string
        :return: new string
        """
        error = ''
        if code[position] == '0':
            error = '1'
        elif code[position] == '1':
            error = '0'
        else:
            print("This code is not in binary format.")
        new_code = code[:position] + error + code[position+1:]
        return new_code

class Reduce(object):
    def __init__(self, reducer_id, output_file):
        self.result = ""
        self.reducer_id = reducer_id
        self.output_file = output_file

    def reduce(self, k, vdict):
        keys = vdict.keys()
        keys.sort()

        for mapper_id in keys:
            encoded_text = vdict[mapper_id]
            # error_string must be len(error_string) % 8 == 0
            out_string = self.to_char(encoded_text)
            self.result += out_string

    def write_to_file(self):
        f = open(self.output_file+"_"+str(self.reducer_id), "wb")
        f.write(self.result)
        f.close()

    def to_char(self, text):
        """
        Convert a binary string to a character string(8 bytes to 1 byte)
        :param text:  binary string         e.g. '0110000101100010'
        :return:      character             e.g. 'ab'
        """
        out_string = ''
        for i in range(int(len(text)/8)):
            # gevent.sleep(0)
            byte_str = text[i*8: (i+1)*8]
            out_string += chr(int(byte_str, 2))
        return out_string



