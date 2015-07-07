# hamming.py
#
# A hamming encoder and decoder
#   Author:     Wei Fang
#   Email:  lancefangw@gmail.com 

import sys


class Hamming(object):

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

    def get_ascii(self, byte_str):
        """
        Get the ascii char from the binary string.
        :param byte_str:  binary string e.g.                e.g.'1100001'
        :return:          the char to the binary string     e.g.'a'
        """
        value = int(byte_str, 2)
        return chr(value)

    def flip(self, position, code):
        """
        Flip the binary code in the position of code.
        :param position: the position you want to flip    e.g. flip(1,'110') = '100'
        :param code: input binary string
        :return: new string
        """
        if code[position] == '0':
            error = '1'
        elif code[position] == '1':
            error = '0'
        else:
            print("This code is not in binary format.")
            sys.exit(0)
        new_code = code[:position] + error + code[position+1:]
        return new_code

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

    def decode(self, e):
        """
        Decode a string(12 chars) using hamming code.
        :param e: 12 bits encoded text      e.g. '011100101010'
        :return: 8 bits decoded text       e.g. '10011010'
        """
        t = e[2] + e[4:7] + e[8:]
        return self.get_ascii(t)

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


class HammingAscii(Hamming):

    def encode_asc(self, text):
        """
        Encode a text to an ascii text using hamming code.
        :param text: original text          e.g. 'ab'
        :return:     encoded ascii text     e.g. '110111010001000011010010'
        """
        encoded_text = ''
        for c in text:
            # Get binary string representation of c
            binary_str = "{0:08b}".format(ord(c))
            # Encode 8bits to 12bits
            new_binary = self.encode(binary_str)
            encoded_text += new_binary
        return encoded_text

    def decode_asc(self, text):
        """
        Decode an ascii text using hamming code.
        :param text: encoded ascii text     e.g. '110111010001000011010010'
        :return:     decoded text           e.g. 'ab'
        """
        out_string = ''
        for i in range(int(len(text)/12)):
            byte_str = text[i*12: (i+1)*12]
            out_string += self.decode(byte_str)
        return out_string

    def chk_asc(self, text):
        """
        Check an ascii text using hamming code.
        :param text:        encoded text        e.g. '010111010001'
        :return bad_list:   bad position list   e.g. [0]
        """
        data_num = len(text) - len(text) % 12
        text = text[:data_num]
        bad_list = []
        byte_pos = 0
        for i in range(int(len(text)/12)):
            byte_str = text[i*12: (i+1)*12]
            bad_flag, bad_pos = self.check(byte_str)
            if bad_flag:
                bad_list.append(bad_pos + byte_pos * 12)
            byte_pos += 1
        return bad_list

    def fix_asc(self, text):
        """
        Fix an ascii text using hamming code.
        :param text: encoded text which may has error.  e.g. '010111010001'
        :return:     fixed text                         e.g. '110111010001'
        """
        bad_list = self.chk_asc(text)
        for i in bad_list:
            text = self.flip(int(i), text)
        return text

    def err_asc(self, pos, text):
        """
        Create an error at bit position.
        :param pos:     Error at the pos
        :param text:    Original text
        :return:        Error text
        """
        return self.flip(int(pos), text)


class HammingBinary(Hamming):

    def encode_bin(self, text):
        """
        Encode a text to an binary text using hamming code.
        :param text: original text
        :return:     encoded binary text
        """
        encoded_text = ''
        for c in text:
            # Get binary string representation of c
            binary_str = "{0:08b}".format(ord(c))
            # Encode 8bits to 12bits
            new_binary = self.encode(binary_str)
            encoded_text += new_binary
        if len(encoded_text) % 8 != 0:
            encoded_text += (8 - len(encoded_text) % 8) * '0'
        out_string = self.to_char(encoded_text)
        return out_string

    def decode_bin(self, text):
        """
        Decode an binary text using hamming code.
        :param text: encoded binary text
        :return:     decoded binary text
        """
        out_string = ''
        text = self.to_bin(text)
        data_num = len(text) - (len(text) % 12)
        text = text[:data_num]
        for i in range(int(len(text)/12)):
            byte_str = text[i*12: (i+1)*12]
            out_string += self.decode(byte_str)
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
            byte_str = text[i*12: (i+1)*12]
            bad_flag, bad_pos = self.check(byte_str)
            if bad_flag:
                bad_list.append(bad_pos + byte_pos * 12)
            byte_pos += 1
        return bad_list

    def fix_bin(self, text):
        """
        Fix an binary text using hamming code.
        :param text: encoded text which may has error.
        :return:     fixed text
        """
        bad_list = self.chk_bin(text)
        text = self.to_bin(text)
        for i in bad_list:
            text = self.flip(int(i), text)
        out_string = self.to_char(text)
        return out_string

    def err_bin(self, pos, text):
        """
        Create an error at bit position.
        :param pos:     Error at the pos
        :param text:    Original text
        :return:        Error text
        """
        text = self.to_bin(text)
        text = self.flip(int(pos), text)
        out_string = self.to_char(text)
        return out_string


class HammingFile(HammingAscii, HammingBinary):

    def __init__(self, type, in_filename='input', out_filename='output', position=0):
        self.type = type
        self.in_filename = in_filename
        self.out_filename = out_filename
        self.position = position

    def encode_file(self):
        """Write the encoded data to the output file."""
        infile = open(self.in_filename)
        outfile = open(self.out_filename, 'w')
        text = infile.read()
        if self.type == 'asc':
            encoded_text = self.encode_asc(text)
        elif self.type == 'bin':
            encoded_text = self.encode_bin(text)
        else:
            print('Wrong type')
            sys.exit(0)
        outfile.write(encoded_text)
        infile.close()
        outfile.close()

    def decode_file(self):
        """Write the decoded data to the output file."""
        infile = open(self.in_filename)        
        outfile = open(self.out_filename, 'w')
        text = infile.read()
        if self.type == 'asc':
            decoded_text = self.decode_asc(text)
        elif self.type == 'bin':
            decoded_text = self.decode_bin(text)
        else:
            print('Wrong type')
            sys.exit(1)
        outfile.write(decoded_text)
        infile.close()
        outfile.close()

    def check_file(self):
        """Check the input data. """
        infile = open(self.in_filename)
        outfile = open(self.out_filename, 'w')
        text = infile.read()
        infile.close()
        if self.type == 'asc':
            bad_list = self.chk_asc(text)
        elif self.type == 'bin':
            bad_list = self.chk_bin(text)
        else:
            print('Wrong type')
            sys.exit(1)
        if len(bad_list) == 0:
            print("All parity bits are valid.")
        else:
            for err_pos in bad_list:
                outfile.write("Error position: %s" % err_pos + '\n')
            outfile.close()
            print 'bad byte positions:'
            print '\n'.join(str(i) for i in bad_list)

    def fix_file(self):
        """Fix the input data."""
        infile = open(self.in_filename)        
        outfile = open(self.out_filename, 'w')
        text = infile.read()
        if self.type == 'asc':
            fixed_text = self.fix_asc(text)
        elif self.type == 'bin':
            fixed_text = self.fix_bin(text)
        else:
            print('Wrong type')
            sys.exit(1)
        outfile.write(fixed_text)
        infile.close()
        outfile.close()

    def err_file(self):
        """Create an error at position <pos>"""
        infile = open(self.in_filename)
        outfile = open(self.out_filename, 'w')
        text = infile.read()
        if self.type == 'asc':
            if int(self.position) > len(text):
                print("Position is greater than the length of data.")
                sys.exit(1)
            err_text = self.err_asc(self.position, text)
        elif self.type == 'bin':
            if int(self.position) > len(text) * 8:
                print("Position is greater than the length of data.")
                sys.exit(1)
            err_text = self.err_bin(self.position, text)
        else:
            print('Wrong type')
            sys.exit(1)
        outfile.write(err_text)
        infile.close()
        outfile.close()


if __name__ == '__main__':
    if len(sys.argv) < 4 or len(sys.argv) > 6:
        print('Wrong number of arguments.')
        sys.exit(1)
    cmd2 = sys.argv[2]
    if len(sys.argv) == 4:
        if cmd2 == 'chk':
            hamFile = HammingFile(sys.argv[1], sys.argv[3])
            hamFile.check_file()
        else:
            print('Use chk when using 3 arguments.')
            sys.exit(1)

    elif len(sys.argv) == 5:
        hamFile = HammingFile(sys.argv[1], sys.argv[3], sys.argv[4])
        if cmd2 == 'enc':
            hamFile.encode_file()
        elif cmd2 == 'dec':
            hamFile.decode_file()
        elif cmd2 == 'fix':
            hamFile.fix_file()
        else:
            print('Use enc/dec/fix when using 4 arguments..')
            sys.exit(1)

    elif len(sys.argv) == 6:
        if cmd2 == 'err':
            hamFile = HammingFile(sys.argv[1], sys.argv[4], sys.argv[5], sys.argv[3])
            hamFile.err_file()
        else:
            print('Use err when using 5 arguments.')
            sys.exit(1)


    







