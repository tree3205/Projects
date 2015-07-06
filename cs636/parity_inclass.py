import sys

class Parity(object):

    def __init__(self, file):
        self.file = file

    def get_parity(self, c):
        s = "{0:08b}".format(ord(c))
        sum = 0
        for b in s:
            if b == '1':
                sum += 1
        return sum % 2

    def encode(self, outfile):
        """Return encoded file and write to outfile."""
        text = self.file.read()
        for c in text:
            s = "{0:08b}".format(ord(c))
            parity = self.get_parity(c)
            outfile.write(str(parity) + s[1:])

    def get_ascii(self, byte_str):
        data = byte_str[1:8]
        value = int(data, 2)
        return chr(value)

    def decode(self):
        """Return decoded file as a string."""
        text = self.file.read()
        out_string = ''
        while len(text) > 0:
            if text[0] == '\n':
                break
            byte_str = text[0:8]
            text = text[8:]
            out_string += self.get_ascii(byte_str)
        return out_string

    def check(self):
        """Check the parity of an encoded file."""
        rv = True
        text = self.file.read()
        while len(text) > 0:
            if text[0] == '\n':
                break
            byte_str = text[0:8]
            text = text[8:]
            c = self.get_ascii(byte_str)
            p = self.get_parity(c)
            if str(p) != byte_str[0]:
                rv = False
                break
        return rv


if __name__ == '__main__':
    cmd = sys.argv[1]

    if cmd == 'encode':
        f = open(sys.argv[2])
        out = open(sys.argv[3], 'w')
        parity = Parity(f)
        parity.encode(out)
        f.close()
        out.close()
    elif cmd == 'decode':
        f = open(sys.argv[2])
        parity = Parity(f)
        print(parity.decode())
        f.close()        
    elif cmd == 'check':
        f = open(sys.argv[2])
        parity = Parity(f)
        print(parity.check())
        f.close()




