# parity.py
#
# A parity encoder and decoder

import sys

class Parity(object):

    def get_parity(self, binary_str):
        """Given binary string (e.g., "0101"), compute the parity.
           Ignore the 8th bit (most significant bit) in the character.
           Return an str: '0' or '1'.
        """
        sum = 0
        for b in binary_str:
            if b == '1':
                sum += 1
        return str(sum % 2)

    def get_ascii(self, byte_str):
        """Get ASCII character from a binary string."""
        data = byte_str[1:8]
        value = int(data, 2)
        return chr(value)


class ParityEncoder(Parity):

    def encode(self, text):
        """Return the parity encoded version of the text string."""

        encode_list = []
        for c in text:
            # Get binary string representation of c
            binary_str = "{0:08b}".format(ord(c))
            # Remove 8th bit
            binary_str = binary_str[1:]
            parity = self.get_parity(binary_str)
            encode_list += str(parity) + binary_str

        encoded_text = ''.join(encode_list)
        return encoded_text


class ParityFileEncoder(ParityEncoder):

    def __init__(self, in_filename, out_filename):
        self.in_filename = in_filename
        self.out_filename = out_filename

    def encode_file(self):
        infile = open(self.in_filename)
        outfile = open(self.out_filename, 'w')
        text = infile.read()
        encoded_text = self.encode(text)
        outfile.write(encoded_text)
        infile.close()
        outfile.close()


class ParityDecoder(Parity):

    def decode(self, text):
        """Return decoded text as a string."""

        out_string = ''

        while len(text) > 0:
            if text[0] == '\n':
                break
            byte_str = text[0:8]
            text = text[8:]
            out_string += self.get_ascii(byte_str)
        return out_string


class ParityFileDecoder(ParityDecoder):

    def __init__(self, in_filename):
        self.in_filename = in_filename

    def decode_file(self):
        """Return decoded file as a string."""
        infile = open(self.in_filename)        
        text = infile.read()
        self.decoded_text = self.decode(text)

    def save(self, out_filename):
        outfile = open(out_filename, 'w')
        outfile.write(self.decoded_text)

    def __str__(self):
        return self.decoded_text


class ParityChecker(Parity):

    def check(self, text):
        """Check the parity of an encoded file.
           Return a list of byte positions with bad parity.
        """
     
        pos = 0
        positions = []

        while len(text) > 0:
            if text[0] == '\n':
                break
            byte_str = text[0:8]
            text = text[8:]
            c = self.get_ascii(byte_str)
            binary_str = "{0:08b}".format(ord(c))
            binary_str[1:]
            p = self.get_parity(binary_str)
            if p != byte_str[0]:
                positions += [pos]
            pos += 1

        return positions

class ParityFileChecker(ParityChecker):

    def __init__(self, in_filename):
        self.in_filename = in_filename

    def check_file(self):
        infile = open(self.in_filename)
        text = infile.read()
        positions = self.check(text)
        return positions


if __name__ == '__main__':
    cmd = sys.argv[1]

    if cmd == 'encode':        
        encoder = ParityFileEncoder(sys.argv[2], sys.argv[3])
        encoder.encode_file()

    elif cmd == 'decode':
        decoder = ParityFileDecoder(sys.argv[2])
        decoder.decode_file()
        if len(sys.argv) == 3:
            sys.stdout.write(str(decoder))
        elif len(sys.argv) == 4:
            decoder.save(sys.argv[3])

    elif cmd == 'check':
        checker = ParityFileChecker(sys.argv[2])
        positions = checker.check_file()
        if positions:
            print("bad byte positions:")
            print(positions)
        else:
            print("All parity bits are valid.")







