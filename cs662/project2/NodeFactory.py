import getopt
import sys
import buildGraph
import SearchQueue

### A NodeFactory is a helper class that is used to create new Nodes.
### It stores the graph representing our problem and uses it to find successors.

class NodeFactory :
    def __init__(self, inputgraph) :
        self.inputgraph = inputgraph

    ### return a list of all nodes reachable from this state
    ### you complete this.
    ### For a given node, find the corresponding vertex in the input graph.
    ### Find the vertices it is connected to, and generate a Node for each
    ### one. Update parentState and cost to reflect the new edge added to the
    ### solution.
    ### nlist is a list of successor nodes.

    def successors(self, oldstate) :
        #add code here
        nlist = []
        graph = self.inputgraph[0]
        vertices = self.inputgraph[1]
        # print graph
        for elem in graph.keys():
            if elem == oldstate.vertex:
                for v in graph[elem]:
                    tmpcost = float(v[1].strip('km'))
                    cost = tmpcost + oldstate.cost
                    vertex = v[0]
                    parentState = oldstate
                    depth = oldstate.depth + 1
                    node = Node(vertex, parentState, cost, depth)
                    #print node
                    nlist.append(node)

        return nlist

class Node:

    def __init__(self, vertex, parentState=None, cost=0, depth=0) :
        self.vertex = vertex
        self.parent = parentState
        self.cost = cost
        self.depth = depth

    def isGoal(self, goalTest) :
        return goalTest(self)

    def isStart(self) :
        return self.parent is None

    def __repr__(self) :
        return self.vertex.__repr__()

    def __hash__(self) :
        return self.vertex.__hash__()

    ## you do this.
    def __lt__(self, other) :
        return self.cost < other

    def __le__(self, other) :
        return not self.cost > other

    def __gt__(self, other) :
        return self.cost > other

    def __ge__(self, other) :
        return not self.cost < other

    def __eq__(self, other) :
        return self.cost == other

    def __ne__(self, other) :
        return self.cost != other

def goalTest(Node=None):
        if Node:
            return Node.vertex == goal
        return False


### search takes as input a search queue, the initial state,
### a node factory,
### a function that returns true if the state provided as input is the goal,
### and the maximum depth to search in the search tree.
### It should print out the solution and the number of nodes enqueued, dequeued,
###  and expanded.
def search(queue, initialState, factory, goalTest, maxdepth=10) :
    closedList = {}
    nodesEnqueued = 1
    nodesDequeued = 0
    nodesExpanded = 0
    queue.insert(initialState)
### you complete this.
### While there are states in the queue,
###   1. Dequeue
###   2. If this is the goal, stop
###   3. If not, insert in the closed list and generate successors
###   4. If successors are not in the closed list, enqueue them.

    while not queue.isEmpty():
        current = queue.pop()
        nodesDequeued += 1
        if goalTest(current):
            break
        else:
            closedList[current.vertex] = True
            neighbour = factory.successors(current)
            nodesExpanded += len(neighbour)
            if current.depth < maxdepth:
                for n in neighbour:
                    if not n.vertex in closedList.keys():
                        queue.insert(n)
                        nodesEnqueued += 1
            else:
                break

    if goalTest(current):
        print 'Find solution == True'
        printSolution(current)
        print 'Number of nodes enqueued: %s' % nodesEnqueued
        print 'Number of nodes dequeued: %s' % nodesDequeued
        print 'Number of nodes expanded: %s' % nodesExpanded
        print '==========='
    else:
        print 'Find solution == False'



    #printSolution(current)



### code for printing out a sequence of states that leads to a solution
def printSolution(node) :
    print "Solution *** "
    print "cost: ", node.cost
    moves = []
    current = node
    while current.parent != None :
        moves.append(current)
        current = current.parent
    moves.append(current)
    moves.reverse()
    for move in moves :
        print move


### usage: search --search=[BFS| DFS | AStar] {-l=depthLimit} {-i}
###               initialState goal infile
###
### If -l is provided, only search to the given depth.
### if -i is provided, use an iterative deepening version (only applies
### to DFS, 10pts extra credit for IDA*)
if __name__ == '__main__':
    # for example if BFS is the input, start by:
    #q = SearchQueue.BFSQueue()

    searchMethod = ''
    iOption = False
    depthLimit = ''
    start = ''
    goal = ''
    inFile= ''

    try:
        optionList, args = getopt.getopt(sys.argv[1:], 'l:i', ['search='])
    except getopt.GetoptError :
        print 'usage: search --search=[BFS| DFS | AStar] {-l=depthLimit} {-i} initialState goal infile'
        sys.exit(0)

    try :
        if optionList:
            for i in optionList:
                if '--search' in i:
                    searchMethod = i[1]
                if '-l' in i:
                    depthLimit = int(i[1].lstrip('='))
                if '-i' in i:
                    iOption = True

        if args:
            start = args[0]
            goal = args[1]
            inFile = args[2]
    except IOError :
        print 'Unable to open ' + args[2]
        sys.exit(0)


    g = buildGraph.Graph()
    inputgraph= g.buildGraph(inFile)

    vertices = inputgraph[1]
    initialState = Node(start)
    goalState = Node(goal)
    factory = NodeFactory(inputgraph)

    if searchMethod == 'BFS':
        q = SearchQueue.BFSQueue()
        if depthLimit:
            search(q, initialState, factory, goalTest, depthLimit)
        else:
            search(q, initialState, factory, goalTest)

    elif searchMethod == 'DFS':
        q = SearchQueue.DFSQueue()
        if iOption:
            d = 1
            while d <= depthLimit:
                search(q, initialState, factory, goalTest, d)
                d += 1
        else:
            if depthLimit:
                search(q, initialState, factory, goalTest, depthLimit)
            else:
                search(q, initialState, factory, goalTest)
    elif searchMethod == 'AStar':
        q = SearchQueue.AStarQueue(goalState, vertices)
        if depthLimit:
            search(q, initialState, factory, goalTest, depthLimit)
        else:
            search(q, initialState, factory, goalTest)

