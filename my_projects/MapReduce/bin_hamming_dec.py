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
            self.table[i] = ""

    def map(self, k, v):
        key = self.partition(self.mapper_id, self.num_mapper)

        text = self.to_bin(v)
        data_num = len(text) - (len(text) % 12)
        text = text[:data_num]
        out_string = ''
        for i in range(int(len(text)/12)):
            # gevent.sleep(0)
            byte_str = text[i*12: (i+1)*12]
            out_string += self.decode(byte_str)
        self.emit(key, out_string)

    def combine(self):
        pass

    def emit(self, key, code):
        self.table[key] += code

    def partition(self, mapper_id, num_mapper):
        # using precious division
        return int(math.floor(mapper_id / (num_mapper / self.num_reducer)))

    # Write the whole dictionary into intermediate file
    def write_to_file(self):
        if debug:
            print "map %d output data" % self.mapper_id, self.table
        for i in range(self.num_reducer):
            pickle.dump(self.table[i], open(self.task_name+'_m'+str(self.mapper_id)+"_r"+str(i), "wb"))

    def decode(self, e):
        """
        Decode a string(12 chars) using hamming code.
        :param e: 12 bits encoded text      e.g. '011100101010'
        :return: 8 bits decoded text       e.g. '10011010'
        """
        t = e[2] + e[4:7] + e[8:]
        return self.get_ascii(t)

    def get_ascii(self, byte_str):
        """
        Get the ascii char from the binary string.
        :param byte_str:  binary string e.g.                e.g.'1100001'
        :return:          the char to the binary string     e.g.'a'
        """
        value = int(byte_str, 2)
        return chr(value)

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


class Reduce(object):
    def __init__(self, reducer_id, output_file):
        self.result = ""
        self.reducer_id = reducer_id
        self.output_file = output_file

    def reduce(self, k, vdict):
        keys = vdict.keys()
        keys.sort()

        for mapper_id in keys:
            decoded_text = vdict[mapper_id]
            self.result += decoded_text

    def write_to_file(self):
        f = open(self.output_file+"_"+str(self.reducer_id), "wb")
        f.write(self.result)
        f.close()



