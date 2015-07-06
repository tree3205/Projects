#!/bin/bash

echo 'Begin tests: heollo.txt:'
echo 'Encoding:'
python hamming.py asc enc ./hello.txt ./enc.asc
echo 'diff enc.asc with hello.asc:'
diff ./hello.asc ./enc.asc
echo 'Finish diff: hello.asc and enc.asc are identical.'
echo 'Encoder success.'
echo '============================'
echo 'Decoding:'
python hamming.py asc dec ./enc.asc ./dec.asc
echo 'diff hello.txt with dec.asc:'
diff ./hello.txt ./dec.asc
echo 'Finish diff: hello.txt and dec.asc are identical.'
echo 'Decoder success.'
echo '============================'
echo 'Create error at 16.'
python hamming.py asc err 16 ./enc.asc ./err.asc
echo 'diff hello.asc with err.asc:'
diff -s ./hello.asc ./err.asc
echo '============================'
echo 'Checker result:'
python hamming.py asc chk ./err.asc
echo '============================'
echo 'Fixing:'
echo 'diff hello.asc with fix.asc'
python hamming.py asc fix ./err.asc ./fix.asc
diff ./hello.asc ./fix.asc
echo 'Fixer success.'

