import cPickle as pickle
import sys
import string

### word frequencies:

### function that takes as input a string and, optionally,
### a dictionary, and returns the dictionary populated with word frequencies.
### provide options to strip punctuation and convert to lowercase.
##

### instr is the input string
### wf is an optional dictionary. This can be used to count over
### multiple files. If it is present, add counts to this.
### stripPunc and toLower indicate whether to strip punctuation and
### convert to lower case.

def readArgs(argv):
    args = argv[1:]
    file = ''
    flags = []
    stripPunc = True
    toLower = True
    output = ''

    for arg in args:
        if arg.startswith('--'):
            flags.append(arg)
        else:
            file = arg

    for flag in flags:
        if flag == '--nostrip':
            stripPunc = False
        if flag == '--noConvert':
            toLower = False
        if flag.startswith('--pfile'):
            tmp = flag.split('=')
            output = tmp[-1]

    try:
        tmp = open(file)
        try:
            instr = tmp.read()
        except IOError:
            print 'Cannot read the file.'
    except IOError:
        print 'Cannot find the file.'
    finally:
        tmp.close()

    return instr, stripPunc, toLower, output

def stripPunctuation(str):
    punc = set(string.punctuation)
    for p in punc:
        if str.endswith(p)|str.startswith(p):
            str = ''.join(char for char in str if char not in punc)

    return str

def wordfreq(instr, wf=None, stripPunc=True, toLower=True) :
    freq = {}
    words = instr.split()

    for word in words:
        if stripPunc == True:
            word = stripPunctuation(word)
        if toLower == True:
            word = word.lower()
        if word in freq.keys():
            freq[word] += 1
        else:
            freq[word] = 1

    return freq

def outPut(freq, output):
    if output == '':
        print freq
    else:
        pickle.dump(freq, open(output, 'wb'))


### Usage: wordfreq {--nostrip --noConvert --pfile=outfile} file
### if --nostrip, don't strip punctuation
### if --noConvert, don't convert everything to lower case
### if --pfile=outfile, pickle the resulting dictionary and store it in outfile.
### otherwise, print it to standard out.

if __name__ == '__main__' :
    instr, stripPunc, toLower, output = readArgs(sys.argv)
    freq = wordfreq(instr, None, stripPunc, toLower)
    outPut(freq, output)



