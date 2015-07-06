# parity_encode.py
#
# A simple program to parity encode an ASCII file.
# The resulting output file will be encoded as "0" and "1" values.
#
# Usage:
#
# python parity_encode.py <input_file> <output_file>

import sys

def get_parity(binary_str):
    """Given binary string (e.g., "0101"), compute the parity.
       Ignore the 8th bit (most significant bit) in the character.
       Return an str: '0' or '1'.
    """
    sum = 0
    for b in binary_str:
        if b == '1':
            sum += 1
    return str(sum % 2)

# Main
infile = open(sys.argv[1])
outfile = open(sys.argv[2], 'w')
text = infile.read()
for c in text:
    # Get the binary string representation of c
    binary_str = "{0:08b}".format(ord(c))
    # Remove the 8th bit
    binary_str = binary_str[1:]
    parity_str = get_parity(binary_str)
    print binary_str
    print parity_str    
    outfile.write(parity_str + binary_str)
infile.close()
outfile.close()



