import cPickle as pickle
import sys
import re

### read in data from an ARFF file and return the following data structures:
### A dict that maps an attribute index to a dictionary mapping attribute names to either:
###    - possible values
###    - the string 'string'
###    - the string 'numeric'
### A list containing all data instances, with each instance stored as a tuple.
def readArgs(argv):
    output = ''
    infile = ''
    args = argv[1:]

    for arg in args:
        if arg.startswith('--pfile'):
            tmp = arg.split('=')
            output = tmp[-1]
        else:
            infile = arg

    return output, infile

def readArff(filehandle):

    attributes = {}
    data = []
    count = 0
    isData = False

    try:
        fileArff = open(filehandle, 'rU')
        try:
            for line in fileArff:
                line = line.strip('\n')
                if line.startswith('@attribute'):
                    count += 1
                    l = ''
                    detail = {}
                    tmp = line.split(' ')
                    if tmp[1].startswith("'"):
                        tmp[1] = tmp[1].replace("'", '')
                    l = re.findall("{[^}]*}", line)
                    s = ''.join(l)

                    if s.startswith("{'"):
                        replace = ['"', "'", '{', '}']
                        for k in replace:
                            s = s.replace(k, '')
                    f = s.lstrip('{').rstrip('}').split(',')
                    f = [s.strip() for s in f]
                    detail[tmp[1]] = f
                    attributes[count] = detail

                if line.startswith('@data'):
                    isData = True
                    continue
                if (line == '' ) | (line.startswith('%')):
                    isData = False
                if isData == True:
                    if line.startswith("'"):
                        line = line.replace("'", '')
                    tmp = line.split(',')
                    arffTuple = tuple(tmp)
                    data.append(arffTuple)

        except IOError:
            print 'Cannot read the file.'
    except IOError:
        print 'The file cannot be open.'
    finally:
        fileArff.close()

    return attributes, data

### Compute ZeroR - that is, the most common data classification without
### examining any of the attributes. Return the most common classification.
def computeZeroR(attributes, data):
    classification = []
    common = {}

    for value in attributes.values():
        d = value.keys()
        if (d[0] == 'class')| (d[0] == 'Class'):
            classification = value[d[0]]

    for key in classification:
        count = 0
        for item in data:
            if item[-1] == key:
                count += 1
        common[count] = key

    max = 0
    for v in common.keys():
        if v > max:
            max = v

    return common[max]

## Usage: readARFF {--pfile=outfile} infile
### If --pfile=outfile, pickle and store the results in outfile. Otherwise,
### print them to standard out. Your code should also call computeZeroR and
### print out the results.


if __name__ == '__main__':
    output, infile = readArgs(sys.argv)
    attributes, data = readArff(infile)
    c = computeZeroR(attributes, data)
    if output == '':
        print 'Attributes: %s' % attributes
        print '==============='
        print 'Data: %s' % data
        print '==============='
        print 'Most common: %s' % c
    else:
        f = open(output, 'wb')
        pickle.dump(attributes,f)
        pickle.dump(data, f)
        pickle.dump(c, f)
        f.close()

        #f = open(output, 'r')
        #p1 = pickle.load(f)
        #p2 = pickle.load(f)
        #p3 = pickle.load(f)
        #print p1
        #print p2
        #print p3
        #f.close()
