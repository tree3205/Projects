# mapreduce_list.py
#
# Collect map results in a list instead of a dictionary.

class Map(object):
    
    def __init__(self):
        self.result_list = []

    def map(self, k, v):
        pass

    def emit(self, k, v):
        self.result_list.append((k,v))

    def get_table(self):
        return self.table

    def get_result_list(self):
        return self.result_list

class Reduce(object):

    def __init__(self):
        self.result_list = []

    def reduce(self, k, vlist):
        pass

    def emit(self, v):
        self.result_list.append(v)

    def get_result_list(self):
        return self.result_list


class Engine(object):
    
    def __init__(self, values, map_class, reduce_class):
            self.values = values
            self.map_class = map_class
            self.reduce_class = reduce_class
            self.results = None

    def execute(self):
        """Execute MapReduce on given input values."""

        # Map phase
        mapper = self.map_class()

        for i, v in enumerate(self.values):
            mapper.map(i, v)

        # Sorting
        result_list = mapper.get_result_list()
        result_list.sort()

        # Reduce phase
        reducer = self.reduce_class()

        # Collect values with the same key for reduce
        k = result_list[0]
        l = []
        for t in result_list:
            if t[0] == k[0]:
                l.append(k[1])
            else:
                reducer.reduce(k[0], l)                
                k = t
                l = []

        self.results = reducer.get_result_list()

    def get_result_list(self):
        return self.results
