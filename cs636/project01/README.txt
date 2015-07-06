README

Test:
cd project01
run chmod +x ./asc_test.sh
run chmod +x ./bin_test.sh

Design:
Split different commands by length of arguments. And construct a
list(positions) to contain the index information about hamming code.Then when
generating parity or decoding bit, just check this list and work with
corresponding bit.

1. For encoding, formating file to 8 bits binary then use encode function in
Parity to generate hamming code. For asc, outputs the hamming code directly
while bin encoding need to add some bit to make total length multiple of 8 then
use chr function to output.

2. For decoding, just split asc encoded file by length 12 while bin need more
step to cut additional bit from encoding. Then according to the position list,
delete all the parity bit and output.

3. For error creator, transform all the codes into a list of every bit, for
example: codes = ['1', '0', '1', '0', ...] and then just bitwise codes[pos - 1].

4. For checker, sum the value of every parity bit and all its corresponding bits
to see if it is even. If so, it is right, otherwise, this parity bit is wrong.
And finally adding all wrong parity bit to get real wrong positions p. But in
outputting the wrong position, need a count to get the which hamming code is
wrong, so as P = 12*count + p, which is the position in the whole input file.

5. For corrector, use checker to get the wrong bit position and bitwise it.










