__author__ = 'WofloW'

# python wc_test.py README.md count_0


import sys
from hamming import HammingFile
from datetime import datetime

def compare(original_text, result):

    f0 = open(original_text, 'rb')
    hamming_result = f0.read()
    f0.close()


    f1 = open(result, 'rb')
    mapreduce_result = f1.read()
    f1.close()

    if hamming_result == mapreduce_result:
        print 'Final result: same\n'
    else:
        print 'Final result: different\n'


if __name__ == "__main__":
    start_time = datetime.now()
    if len(sys.argv) < 6 or len(sys.argv) > 7:
        print('Wrong number of arguments.')
        sys.exit(1)
    cmd2 = sys.argv[2]
    if len(sys.argv) == 6:
        type = sys.argv[1]
        input_filename = sys.argv[3]
        output_filename = sys.argv[4]
        mapreduce_filename = sys.argv[5]
        hamFile = HammingFile(type, input_filename, output_filename)
        if cmd2 == 'chk':
            hamFile.check_file()
        elif cmd2 == 'enc':
            hamFile.encode_file()
        elif cmd2 == 'dec':
            hamFile.decode_file()
        elif cmd2 == 'fix':
            hamFile.fix_file()
        else:
            print('Use chk/enc/dec/fix when using 4 arguments..')
            sys.exit(1)
        end_time = datetime.now()
        print('In memory duration: {}'.format(end_time - start_time))
        compare(output_filename, mapreduce_filename)

    elif len(sys.argv) == 7:
        if cmd2 == 'err':
            type = sys.argv[1]
            input_filename = sys.argv[4]
            output_filename = sys.argv[5]
            pos = sys.argv[3]
            mapreduce_filename = sys.argv[6]
            hamFile = HammingFile(sys.argv[1], sys.argv[4], sys.argv[5], pos)
            hamFile.err_file()
            end_time = datetime.now()
            print('In memory duration: {}'.format(end_time - start_time))
            compare(output_filename, mapreduce_filename)
        else:
            print('Use err when using 5 arguments.')
            sys.exit(1)


