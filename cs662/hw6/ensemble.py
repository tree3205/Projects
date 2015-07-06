__author__ = 'yxu66'
import readARFF
import decisionTree as dt
import random
import collections as coll
import sys

def resample(data):
    N = len(data)
    new_data =[]
    for i in range(N):
        ranNum = random.randrange(N)
        new_data.append(data[ranNum])

    return new_data

def compute_score(testData, ensemble, attrs, defaultVal):
    score = 0.0
    for x in testData:
        label_counts = coll.Counter()
        for c in ensemble:
            label = c.classify(x, attrs, defaultVal)
            label_counts += 1
        if label_counts.most_common(1)[0][0] == x[-1]:
            score += 1
    print score/len(testData)
    return score/len(testData)

def bagging(data, attrs, numTrees, defaultValue, test_data):
    ensemble = []
    attrNames = readARFF.getAttrList(attrs)
    for i in range(numTrees):
        newData = resample(data)
        tree = dt.makeTree(newData, attrs, defaultValue)
        ensemble.append(tree)
    print 'Test score:'
    compute_score(test_data, ensemble, attrs, defaultValue)
    print 'Train score:'
    compute_score(data, ensemble, attrs, defaultValue)

def renormalize(weights):
    new_weights =  []
    sum_weights = float(sum(weights))
    for i in weights:
        new_weights.append(i/sum_weights)
    return new_weights

def boosting(data, attrs, numTrees, test_data):
    N = len(data)
    w = []
    for i in range(N):
        w.append(1.0/N)
    pass

def create_weighted_set(data, weights):
    results = []