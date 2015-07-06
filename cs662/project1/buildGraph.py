import cPickle as pickle
import sys



### build graph:
### takes as input a file in the form:
## a b dist time
### where a and b are destinations, dist is the distance between them, and
### time is the time needed to travel between them and constructs a graph.

### This graph should be represented as an adjacency list, and stored as a
### dictionary, with the key in the dictionary being the source of an edge and
### the value being a tuple containing the destination, distance, and cost.
### For example:
### g[a] = (b,dist,time)

class Graph:
    def __init__(self, infile=None) :
        self.adjlist = {}
        if infile :
            self.buildGraph(infile)

    def readArgs(self, argv):
        """buildGraph {--pfile=outfile} {-d=startNode} infile"""
        args = argv[1:]
        output = ''
        startNode = ''
        infile = ''
        for arg in args:
            if arg.startswith('--pfile'):
                tmp = arg.split('=')
                output = tmp[-1]
            if arg.startswith('-d'):
                tmp = arg.split('=')
                startNode = tmp[-1]
            else:
                infile = arg

        return output, startNode, infile

    ### method to print a graph.
    def __repr__(self) :
        return '%s' % self.adjlist

    ### helper methods to construct edges and vertices. Use these in buildGraph.
    def createVertex(self, inStr) :
        name, lat,longitude = inStr.split(" ",2)
        lat = lat.split("=")[1]
        longitude = longitude.split("=")[1]
        return Vertex(name, lat, longitude)

    def createEdges(self, inStr) :
        src, dest, dist, time = inStr.split(" ",4)
        dist=dist.split("=")[1]
        time=time.split("=")[1]
        e1 = Edge(src,dest,dist, time)
        e2 = Edge(dest,src,dist, time)
        return e1, e2

### method that takes as input a file name and constructs the graph described
### above.
    def buildGraph(self, infile) :
        inFile = ''
        vertices = []
        isVertex = False
        isEdges = False

        try:
            inFile = open(infile, 'rb')
            try:
                for line in inFile:
                    line = line.strip('\n')
                    if line.startswith('## vertices'):
                        isVertex = True
                        continue
                    if line.startswith('## edges'):
                        isEdges = True
                        isVertex = False
                        continue
                    if (isVertex == True) & (isEdges == False) :
                        vertex = self.createVertex(line)
                        vertices.append(vertex.name)
                    if (isVertex == False) & (isEdges == True) :
                        e1, e2 = self.createEdges(line)
                        src = e1.src
                        dest =e1.dest
                        distance = e1.distance
                        time = e1.time
                        if src in self.adjlist.keys():
                            self.adjlist[src].append((dest, distance, time))
                        else:
                            self.adjlist[src]=[(dest, distance, time)]
                        if dest in self.adjlist.keys():
                            self.adjlist[dest].append((src, distance, time))
                        else:
                            self.adjlist[dest] = [(src, distance, time)]
            except IOError:
                print 'Cannot read the file.'
        except IOError:
            print 'Cannot find the file.'
        finally:
            inFile.close()

        return self.adjlist

### this method should take as input the name of a starting vertex
### and compute Dijkstra's algorithm,
### returning a dictionary that maps destination cities to
### a tuple containing the length of the path, and the vertices that form the path.
### Wikipedia has pseudo-Code for this - now translate it to Python,
### But do NOT copy any actual python code from anywhere else on the web
    def dijkstra(self, source) :
        """g[a] = (b,dist,time)->{dest: (distance, [src, ..., dest])}"""
        dk = {}
        pathInfo = {}
        cost = 0
        path = []
        for k in self.adjlist.keys():
            if k == source:
                dk[k] = [False, 0, '']
            else :
                dk[k] = [False, float('inf'), '']

        for v in self.adjlist.keys():
            u = self.findMin(dk)
            dk[u][0] = True
            for e in self.adjlist[u]:
                cost = e[1]

                if dk[e[0]][1] > float(cost[:-2]) + float(dk[u][1]):
                    dk[e[0]][1] = float(cost[:-2]) + float(dk[u][1])
                    dk[e[0]][2] = u

        for k, v in dk.items():
            path = []
            self.findPath(source, k, dk, path)
            pathInfo[k] = (dk[k][1], path)

        return pathInfo


    def findPath(self, source, k, dk, path):
        if k == source:
            path.append(k)
        else:
            self.findPath(source, dk[k][2], dk, path)
            path.append(k)


    def findMin(self, dk):
        minV = -1
        minDist = float('inf')
        for k, v in dk.items():
            if (v[0] == False) & (v[1] < minDist):
                minDist = v[1]
                minV = k
        return minV

### classes representing vertices and edges
class Vertex:
    def __init__(self, name, lat, longitude) :
        self.name = name
        self.lat = lat
        self.longitude = longitude
    def __hash__(self) :
        return hash(self.name)
    def __eq__(self, other) :
        return self.name == other.name


class Edge:
    def __init__(self, src, dest, distance, time) :
        self.src = src
        self.dest = dest
        self.distance = distance
        self.time = time


### usage: buildGraph {--pfile=outfile} {-d=startNode} infile
### if --pfile=outfile is provided, write a pickled version of the graph
### to outfile. Otherwise, print it to standard output.
### if --d=startNode is provided, compute dijkstra with the given starting node
###  as source

if __name__ == '__main__' :
    g = Graph()
    output, startNode, infile = g.readArgs(sys.argv)
    g.adjlist = g.buildGraph(infile)
    pathInfo = g.dijkstra(startNode)
    if output != '':
        f = open(output, 'wb')
        pickle.dump(g.adjlist, f)
        pickle.dump(pathInfo, f)
        f.close()

        #f = open(output, 'r')
        #p1 = pickle.load(f)
        #p2 = pickle.load(f)
        #print p1
        #print p2
        #f.close()
    else:
        print 'Graph: %s' % g.adjlist
        print '==============='
        print 'Path: %s' % g.dijkstra(startNode)
