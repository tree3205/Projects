import heapq
import math
### this is a helper function that converts a string containing latitude or longitude
## represented as degrees.minutes.seconds (e.g. 37.47.44N) into a float.

def convertLatLong(str) :
    deg, minutes,seconds = str[:-1].split('.',2)
    minutes = float(minutes) + (float(seconds) / 60.0)
    deg = float(deg) + (minutes/ 60.0)
    return deg

class SearchQueue :
    def __init__(self) :
        self.q = []

    def insert(self, item) :
        pass

    def pop(self) :
        pass

    def isEmpty(self) :
        return self.q == []

### you complete this.
class BFSQueue(SearchQueue) :

    def insert(self, item):
        return self.q.append(item)

    def pop(self):
        return self.q.pop(0)

### you complete this.
class DFSQueue(SearchQueue) :

    def insert(self, item):
        return self.q.insert(0, item)

    def pop(self):
        return self.q.pop(0)


### you complete this
class AStarQueue(SearchQueue) :

    def __init__(self, goalState, vertices):
        SearchQueue.__init__(self)
        self.goalState = goalState
        self.vertices = vertices

    def caculatef(self, current):
        current_lat = 0
        current_long = 0
        goal_lat = 0
        goal_long = 0
        item = ()

        for k, v in self.vertices.items():
            if k == current.vertex:
                current_lat = convertLatLong(v[0])
                current_long = convertLatLong(v[1])
            if k == self.goalState.vertex:
                goal_lat = convertLatLong(v[0])
                goal_long =convertLatLong(v[1])
        #print 'c-lat: %s'% current_lat
        #print 'c-long: %s'% current_long
        #print 'g-lat: %s'% goal_lat
        #print 'g-lat: %s'% goal_long
        tmp = math.pow(current_lat-goal_lat, 2)+math.pow(current_long-goal_long, 2)
        h = math.pow(tmp, 0.5)
        g = current.cost
        f = g + h
        item = (f, current)
        #print f
        #print item
        return item

    def insert(self, item):
        fitem = self.caculatef(item)
        heapq.heappush(self.q, fitem)

    def pop(self):
        return heapq.heappop(self.q)[1]


