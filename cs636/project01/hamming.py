import sys
import math

# Project implements a Hamming Code encoder, decoder, error finder,
# and error corrector
#
# For <type>:
# asc - ASCII encoding
# bin - Binary encoding
#
# For <command>:
# enc <infile> <outfile> - Encode
# dec <infile> <outfile> - Decode
# chk <infile>  - Check
# fix <infile> <outfile> - Fix
# err <pos> <infile> <outfile> - Create an error at bit position <pos>
#
# Usage: python hamming.py <type> <command> (<pos>) <arg1> (<arg2>)
# For example: $ python hamming.py asc enc hello.txt hello.asc

""" Generate parity bit and decode it back.  """
class Parity(object):

    # The list of positions use to generate hamming code.
    def __init__(self):
        self.parity_pos = [1, 2, 4, 8]
        self.p1_pos = [3, 5, 7, 9, 11]
        self.p2_pos = [3, 6, 7, 10, 11]
        self.p4_pos = [5, 6, 7, 12]
        self.p8_pos = [9, 10, 11, 12]
        self.positions = [self.p1_pos, self.p2_pos, self.p4_pos, self.p8_pos]

    # Receive: mid-data like: [None, None, '0', None, '1', '1', '0', None, '0', '0', '0', '1']
    # Return: hamming code.
    def gen_parity_bit(self, data):
        parity = 0
        for p in self.parity_pos:
            index = int(math.log(p, 2))
            for i in self.positions[index]:
                parity ^= int(data[i - 1])
            data[p - 1] = str(parity)
            parity = 0
        return data

    # Receive: Single 8 digits like: '01100001'
    # Return: Single hamming code like: : ['1', '1', '0', '1', '1', '1', '0', '1', '0', '0', '0', '1']
    def parity_encode(self, char):
        data = []
        count = 0
        """Store char in the form of: [None, None, '0', None, '1', '1', '0', None, '0', '0', '0', '1'] """
        for c in char:
            while count + 1 in self.parity_pos:
                data.append(None)
                count += 1
            data.append(c)
            count += 1
        code = self.gen_parity_bit(data)
        return code

    # Decode hamming code.
    # Receive: hamming code(str)
    # Return: original str
    def parity_decode(self, code):
        orig_code = ''.join([code[i] for i in range(12) if not i+1 in self.parity_pos])
        return orig_code

class HammingAscii(Parity):

    def encode(self, char):
        asc_code = self.parity_encode(char)
        return asc_code

    def decode(self, code):
        char = self.parity_decode(code)
        return char

    #  Check the parity of an encoded file.
    #  Return:
    #  has_err: boolean value of if has error
    #  err_pos: the positions with bad parity.
    def chk(self, code):
        err_pos = 0
        has_err = False
        for p in self.parity_pos:
            index = int(math.log(p, 2))
            sum = int(code[p - 1])
            for i in self.positions[index]:
                sum += int(code[i - 1])
            if sum % 2 != 0:
                err_pos += p
                has_err = True
        return has_err, err_pos

    # Fix the error of an encoded file.
    # Receive: single hamming code
    # Return: corrected single hamming code
    def fix(self, code):
        has_err, err_pos = self.chk(code)
        if has_err:
            val = int(code[err_pos - 1])
            val ^= 1
            code[err_pos - 1] = str(val)
        return code

    # Receive: totoal code list(['1', '0',...])
    # Return: total code list
    def err(self, pos, code):
        err_pos = int(pos)
        val = int(code[err_pos - 1])
        val ^= 1
        code[err_pos - 1] = str(val)
        return code

class HammingBinary(Parity):

    def encode(self, char):
        bin_code = self.parity_encode(char)
        return bin_code

    def decode(self, code):
        orig_data = ''
        char = self.parity_decode(code)
        orig_data += chr(int(char, 2))
        return orig_data

    #  Check the parity of an encoded file.
    #  Return:
    #  has_err: boolean value of if has error
    #  err_pos: the positions with bad parity.
    def chk(self, code):
        err_pos = 0
        has_err = False
        for p in self.parity_pos:
            index = int(math.log(p, 2))
            sum = int(code[p - 1])
            for i in self.positions[index]:
                sum += int(code[i - 1])
            if sum % 2 != 0:
                err_pos += p
                has_err = True
        return has_err, err_pos

    # Fix the error of an encoded file.
    # Receive: single hamming code
    # Return: corrected single hamming code
    def fix(self, code):
        has_err, err_pos = self.chk(code)
        if has_err:
            val = int(code[err_pos - 1])
            val ^= 1
            code[err_pos - 1] = str(val)
        return code

    # Receive: totoal code list(['1', '0',...])
    # Return: total code list
    def err(self, pos, code):
        err_pos = int(pos)
        val = int(code[err_pos - 1])
        val ^= 1
        code[err_pos - 1] = str(val)
        return code

""" Read arguments from command line,
    and read or write file as ascii or binary """
class HammingFile():

    def __init__(self, in_file, out_file):
        self.in_file = in_file
        self.out_file = out_file

    # Read encoded asc file.
    # Receive: hamming code(str)
    # Return: lis of hamming code
    def read_asc_code(self, codes):
        codes_list = []
        codes = codes.strip()
        for i in xrange(0, len(codes), 12):
            code = codes[i:i + 12]
            codes_list.append(code)
        return codes_list

    # Read encoded bin file.
    # Receive: total codes str
    # Return: totoal code list
    def read_bin_code(self, codes):
        data = ''
        codes_list = []

        for code in codes:
            c = "{0:08b}".format(ord(code))
            data += c
        num = len(data) % 12
        if num != 0:
            orig_code = data[:-num]
        else:
            orig_code = data
        for i in xrange(0, len(orig_code), 12):
            codes_list.append(orig_code[i: i + 12])
        return codes_list

    # Write encoded asc file
    # Receive: Code list
    # output: By literally writing single code String
    def write_enc_asc(self, code):
        str_code = ''
        for c in code:
            str_code += c
        out_file.write(str_code)

    # Write decoded asc file
    def write_dec_asc(self, code):
        str = chr(int(code, 2))
        out_file.write(str)

    # Write encoded bin file.
    # Receive: total code list
    # Output: By literally writing single code String
    def write_enc_bin(self, code_list):
        str = ''
        for s in code_list:
            str += s
        num = len(str) % 8
        while num != 0:
            str += '0'
            num -= 1
        for x in xrange(0, len(str), 8):
            out_file.write(chr(int(str[x: x+8], 2)))

    # Write decoded bin file.
    def write_dec_bin(self, orig_data):
        out_file.write(orig_data)

    def to_bin(self, codes):
        data = ''
        for code in codes:
            c = "{0:08b}".format(ord(code))
            data += c
        return data


if __name__ == '__main__':
    type = sys.argv[1]
    cmd = sys.argv[2]
    hamming_asc = HammingAscii()
    hamming_bin = HammingBinary()

    """ Checker, no output. """
    if len(sys.argv) == 4:
        in_file = open(sys.argv[3])
        f = HammingFile(in_file, None)
        codes = in_file.read()
        err_positions = ''
        count = 0
        if type == 'asc':
            codes_list = f.read_asc_code(codes)
            for code in codes_list:
                has_err, err_pos = hamming_asc.chk(code)
                if has_err:
                    err_positions += str(int(err_pos) + count*12)
                count += 1
            if err_positions:
                print("Bad byte positions:")
                print(err_positions)
            else:
                print("All parity bits are valid.")
        else:
            codes_list = f.read_bin_code(codes)
            for code in codes_list:
                has_err, err_pos = hamming_bin.chk(code)
                if has_err:
                    err_positions += str(int(err_pos) + count*12)
                count += 1
            if err_positions:
                print("Bad byte positions:")
                print(err_positions)
            else:
                print("All parity bits are valid.")
    elif len(sys.argv) == 5:
        in_file = open(sys.argv[3])
        out_file = open(sys.argv[4], 'w')
        f = HammingFile(in_file, out_file)
        if cmd == 'enc':
            """ Encoder, assume read file is like'abd'. """
            text = in_file.read()
            codes_list = []
            for s in text:
                c = "{0:08b}".format(ord(s))
                code = hamming_bin.encode(c)
                if type == 'asc':
                    if type == 'asc':
                        code = hamming_asc.encode(c)
                        f.write_enc_asc(code)
                else:
                    code = hamming_bin.encode(c)
                    codes_list += code
            f.write_enc_bin(codes_list)
        elif cmd == 'dec':
            """ Decoder """
            codes = in_file.read()
            if type == 'asc':
                codes_list = f.read_asc_code(codes)
                for code in codes_list:
                    text = hamming_asc.decode(code)
                    f.write_dec_asc(text)
            else:
                codes_list = f.read_bin_code(codes)
                for code in codes_list:
                    text = hamming_bin.decode(code)
                    f.write_dec_bin(text)
        else:
            """ Corrector """
            codes = in_file.read()
            total_codes = []
            new_code_list = []
            if type == 'asc':
                codes_list = f.read_asc_code(codes)
                for code in codes_list:
                    for e in code:
                        total_codes.append(e)
                for x in xrange(0, len(total_codes), 12):
                    new_code = hamming_asc.fix(total_codes[x: x + 12])
                    f.write_enc_asc(new_code)
            else:
                codes_list = f.read_bin_code(codes)
                for code in codes_list:
                    for e in code:
                        total_codes.append(e)
                for x in xrange(0, len(total_codes), 12):
                    new_code = hamming_bin.fix(total_codes[x: x + 12])
                    new_code_list += new_code
                f.write_enc_bin(new_code_list)
        in_file.close()
        out_file.close()
    else:
        """ Error Creator """
        pos = sys.argv[3]
        in_file = open(sys.argv[4])
        out_file = open(sys.argv[5], 'w')
        f = HammingFile(in_file, out_file)
        codes = in_file.read()
        total_codes = []
        if type == 'asc':
            codes_list = f.read_asc_code(codes)
            for elem in codes_list:
                for e in elem:
                    total_codes.append(e)
            err_code_list = hamming_asc.err(pos, total_codes)
            for err_code in err_code_list:
                f.write_enc_asc(err_code)
        else:
            # codes_list = f.read_bin_code(codes)
            # for elem in codes_list:
            #     for e in elem:
            #         total_codes.append(e)
            # err_code_list = hamming_bin.err(pos, total_codes)
            # f.write_enc_bin(err_code_list)

            codes = f.to_bin(codes)
            for c in codes:
                total_codes.append(c)
            err_code_list = hamming_bin.err(pos, total_codes)
            f.write_enc_bin(err_code_list)


