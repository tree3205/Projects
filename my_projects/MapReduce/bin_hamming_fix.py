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

        bad_list = self.chk_bin(v)
        text = self.to_bin(v)
        for err_pos in bad_list:
            # gevent.sleep(0)
            if debug:
                print "text: %s" % text
                print "mapper %d" % self.mapper_id
                print "err_pos: %d" % err_pos
            text = self.flip(err_pos, text)
        out_string = self.to_char(text)
        self.emit(key, out_string)

    def combine(self):
        pass

    def emit(self, key, out_string):
        self.table[key] += out_string

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

    def to_char(self, text):
        """
        Convert a binary string to a character string(8 bytes to 1 byte)
        :param text:  binary string         e.g. '0110000101100010'
        :return:      character             e.g. 'ab'
        """
        out_string = ''
        for i in range(int(len(text)/8)):
            byte_str = text[i*8: (i+1)*8]
            out_string += chr(int(byte_str, 2))
        return out_string

    def chk_bin(self, text):
        """
        Check an binary text using hamming code.
        :param text:        encoded binary text
        :return bad_list:   bad position list
        """
        text = self.to_bin(text)
        data_num = len(text) - len(text) % 12
        text = text[:data_num]
        bad_list = []
        byte_pos = 0
        for i in range(int(len(text)/12)):
            # gevent.sleep(0)
            byte_str = text[i*12: (i+1)*12]
            bad_flag, bad_pos = self.check(byte_str)
            if bad_flag:
                bad_list.append(bad_pos + byte_pos * 12)
            byte_pos += 1
        return bad_list

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

    def flip(self, position, code):
        """
        Flip the binary code in the position of code.
        :param position: the position you want to flip    e.g. flip(1,'110') = '100'
        :param code: input binary string
        :return: new string
        """
        error = ""
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
            decoded_text = vdict[mapper_id]
            self.result += decoded_text

    def write_to_file(self):
        f = open(self.output_file+"_"+str(self.reducer_id), "wb")
        f.write(self.result)
        f.close()


