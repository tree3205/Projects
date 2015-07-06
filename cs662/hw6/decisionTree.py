__author__ = 'yxu66'

import cPickle as pickle
import sys, math, re
import readARFF


dict = []

### takes as input a list of class labels. Returns a float
### indicating the entropy in this data.
def entropy(data) :
    e = 0.0
    possibleValues = set(data)
    for value in possibleValues:
        c = [item for item in data].count(value)
        e += -(float(c) / len(data)) * math.log((float(c) / len(data)), 2)
    return e


### Compute remainder - this is the amount of entropy left in the data after
### we split on a particular attribute. Let's assume the input data is of
### the form:
###    [(value1, class1), (value2, class2), ..., (valuen, classn)]
def remainder(data) :
    possibleValues = set([item[0] for item in data])
    r = 0.0
    for value in possibleValues :
        c = [item[0] for item in data].count(value)
        r += (float(c) / len(data) ) * entropy([item[1] for item in
                                                data if item[0] == value])
    return r


### selectAttribute: choose the index of the attribute in the current
### dataset that minimizes the remainder.
### data is in the form [[a1, a2, ..., c1], [b1,b2,...,c2], ... ]
### where the a's are attribute values and the c's are classifications.
### And attributes is a dictionary {index: {attribute: [v1,v2,...,vn]}},

def selectAttribute(data, attributes) :
    min = 100
    minIndex = 0

    for index in attributes.keys():
        d = [(item[index], item[-1]) for item in data]
        r = remainder(d)
        if r < min:
            min = r
            minIndex = index
    return minIndex


### a TreeNode is an object that has either:
### 1. An attribute to be tested and a set of children; one for each possible
### value of the attribute.
### 2. A value (if it is a leaf in a tree)
class TreeNode :
    def __init__(self, attribute, value) :
        self.attribute = attribute
        self.value = value
        self.children = {}

    def __repr__(self) :
        if self.attribute :
            return self.attribute
        else :
            return self.value

    ### a node with no children is a leaf
    def isLeaf(self) :
        return self.children == {}

    ### return the value for the given data
    ### the input will be:
    ### data - an object to classify - [v1, v2, ..., vn]
    ### attributes - the attribute dictionary
    def classify(self, data, attributes) :
        #If we are at a leaf, return the value of that leaf.
        # Otherwise, check which attribute this node tests and follow the appropriate child.
        # If there is no child for this value, return a default value.
        if self.isLeaf():
            return self.value
        else:
            for index in attributes:
                if attributes[index].keys()[0] == self.attribute:
                    return self.children[data[index]].classify(data, attributes)


### a tree is simply a data structure composed of nodes (of type TreeNode).
### The root of the tree
### is itself a node, so we don't need a separate 'Tree' class. We
### just need a function that takes in a dataset and our attribute dictionary,
### builds a tree, and returns the root node.
### makeTree is a recursive function. Our base case is that our
### dataset has entropy 0 - no further tests have to be made. There
### are two other degenerate base cases: when there is no more data to
### use, and when we have no data for a particular value. In this case
### we use either default value or majority value.
### The recursive step is to select the attribute that most increases
### the gain and split on that.


### assume: input looks like this:
### dataset: [[v1, v2, ..., vn, c1], [v1,v2, ..., c2] ... ]
### attributes: [a1,a2,...,an] }
def makeTree(dataset, attributes, defaultValue) :
    # def makeTree(dataset) :
    #   if data is empty:
    #       return a single Node with defaultValue
    #   if there are no attributes left to test:
    #       return a single Node with majority classification
    #   if all data are in the same class :
    #       return a single Node with that classification
    #   if our dataset has entropy 0:
    #       return a single Node with that classification
    #   else :
    #       select the attribute that produces the largest information gain
    #       split the dataset according to the values of this attribute to create v smaller datasets.
    #       create a new Node - each child will be created by calling makeTree with one on the v subsets.

    node = TreeNode(None, None)

    if len(dataset) == 0:
        node.value = defaultValue
        return node
    if len(attributes) == 0:
        classification = readARFF.computeZeroR(attributes, dataset)
        node.value = classification
        return node
    if entropy([d[-1] for d in dataset]) == 0:
        node.value = dataset[0][-1]
        return node

    selectIndex = selectAttribute(dataset, attributes)
    key = attributes[selectIndex].keys()[0]
    values = attributes[selectIndex][key]
    node.attribute = key
    next_attributes = attributes.copy()
    del next_attributes[selectIndex]

    for v in values:
        childrenList = [d for d in dataset if d[selectIndex] == v]
        defaultValue = readARFF.computeZeroR(attributes, dataset)
        node.children[v] = makeTree(childrenList, next_attributes, defaultValue)

    return node

def printTree(root):
    if root.isLeaf() == False:
        tmp = []
        tmp.append(root)
        tmp.append(root.children)
        dict.append(tmp)
        for c in root.children:
            if root.children[c].isLeaf() == False:
                printTree(root.children[c])





