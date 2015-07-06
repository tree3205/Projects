#!/bin/bash

echo 'Begin tests: heollo.txt:'
echo 'Encoding:'
python hamming.py bin enc ./hello.txt ./enc.bin
echo 'diff enc.bin with hello.bin:'
diff ./hello.bin ./enc.bin
echo 'Finish diff: hello.bin and enc.bin are identical.'
echo 'Encoder success.'
echo '============================'
echo 'Decoding:'
python hamming.py bin dec ./enc.bin ./dec.bin
echo 'diff hello.txt with dec.bin:'
diff ./hello.txt ./dec.bin
echo 'Finish diff: hello.txt and dec.bin are identical.'
echo 'Decoder success.'
echo '============================'
echo 'Create error at 16.'
python hamming.py bin err 16 ./enc.bin ./err.bin
echo 'diff hello.bin with err.bin:'
diff -s ./hello.bin ./err.bin
echo '============================'
echo 'Checker result:'
python hamming.py bin chk ./err.bin
echo '============================'
echo 'Fixing:'
echo 'diff hello.bin with fix.bin'
python hamming.py bin fix ./err.bin ./fix.bin
diff ./hello.bin ./fix.bin
echo 'Fixer success.'

