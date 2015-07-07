import cPickle as pickle
import string

unit = ' '
debug = True


class Map(object):
    def __init__(self, mapper_id, num_mapper, num_reducer, task_name, split_size):
        self.table = {}
        for i in range(num_reducer):
            self.table[i] = {}
        self.task_name = task_name
        self.mapper_id = mapper_id
        self.num_mapper = num_mapper
        self.num_reducer = num_reducer
        self.split_size = split_size

    def map(self, k, v):
        v = v.replace('\n', ' ').replace('\r', ' ')
        words = v.split()
        for w in words:
            word = w.strip(string.punctuation)
            if word != '':
                self.emit(word, 1)

    def emit(self, k, v):
        reducer_dict = self.table[self.partition(k)]
        if k in reducer_dict:
            reducer_dict[k].append(v)
        else:
            reducer_dict[k] = [v]

    def partition(self, key):
        return hash(key) % self.num_reducer

    def combine(self):
        for reducer in self.table:
            for word in self.table[reducer]:
                self.table[reducer][word] = sum(self.table[reducer][word])

    def write_to_file(self):
        for i in range(self.num_reducer):
            pickle.dump(self.table[i], open(self.task_name+'_m'+str(self.mapper_id)+"_r"+str(i), "wb"))


class Reduce(object):
    def __init__(self, reducer_id, output_file):
        self.table = {}
        self.reducer_id = reducer_id
        self.output_file = output_file

    def reduce(self, k, vdict):
        for mapper_id in vdict:
            for word in vdict[mapper_id]:
                if word in self.table:
                    self.emit(word, self.table[word] + vdict[mapper_id][word])
                else:
                    self.emit(word, vdict[mapper_id][word])

    def emit(self, k, v):
        self.table[k] = v

    def write_to_file(self):
        f = open(self.output_file+"_"+str(self.reducer_id), "wb")
        keys = self.table.keys()
        keys.sort()

        for word in keys:
            f.write(word + ": " + str(self.table[word]) + '\n')
        f.close()
