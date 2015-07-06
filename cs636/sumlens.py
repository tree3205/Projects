import mapreduce

class SumLenMap(mapreduce.Map):

    def map(self, k, v):
        self.emit('0', str(len(v)))

class SumLenReduce(mapreduce.Reduce):

    def reduce(self, k, vlist):
        total = 0
        for v in vlist:
            total = total + int(v)
        self.emit(str(total))

if __name__ == '__main__':

    values = ['foo', 'bar', 'baz']
    engine = mapreduce.Engine(values, SumLenMap, SumLenReduce)
    engine.execute()
    result_list = engine.get_result_list()
    
    for r in result_list:
        print r
