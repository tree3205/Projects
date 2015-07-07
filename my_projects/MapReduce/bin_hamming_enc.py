from __future__ import division
import math
import cPickle as pickle
import gevent

unit = 2
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
        # if debug:
        #     print "map %d input data" % self.mapper_id, v
        out_string = ''
        for char in v:
            # gevent.sleep(0)
            binary_str = "{0:08b}".format(ord(char))
            out_string += self.encode(binary_str)
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
        # if debug:
        #     print "map %d output data" % self.mapper_id, self.table
        for i in range(self.num_reducer):
            pickle.dump(self.table[i], open(self.task_name+'_m'+str(self.mapper_id)+"_r"+str(i), "wb"))

    def encode(self, e):
        """
        Encode a string(8 chars) using hamming code.
        :param e: 8 bits original text      e.g. '10011010'
        :return: 12 bits encoded text       e.g. '011100101010'
        """
        t0 = self.get_parity(e[0] + e[1] + e[3] + e[4] + e[6])
        t1 = self.get_parity(e[0] + e[2] + e[3] + e[5] + e[6])
        t3 = self.get_parity(e[1] + e[2] + e[3] + e[7])
        t7 = self.get_parity(e[4] + e[5] + e[6] + e[7])
        t = t0 + t1 + e[0] + t3 + e[1:4] + t7 + e[4:]
        return t

    def get_parity(self, binary_str):
        """
        Get the parity bit from the binary string.
        :param binary_str:  binary string   e.g. '0101'
        :return:            Return an str '0' or '1' e.g. '0' = (0 + 1 + 0 +1) % 2.
        """
        sum = 0
        for b in binary_str:
            if b == '1':
                sum += 1
        return str(sum % 2)


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
            # This should only happen in last reducer
            if len(encoded_text) % 8 != 0:
                encoded_text += (8 - len(encoded_text) % 8) * '0'
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



