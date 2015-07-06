from __future__ import division

import sys, random
import decisionTree
import readARFF

__author__ = 'yxu66'

# A method to calculate the precision recall and accuracy for decision tree
def eval_performance(tree, tests, classifications):
    result = {}
    predict_results = []
    actual_results = []
    for t in tests:
        predict_results.append(tree.classify(t, attrs))
        actual_results.append(t[-1])
    for c in classifications:
        precision, recall = calculate(predict_results, actual_results, c)
        result[c] = (precision, recall)

    accuracy = cal_accuracy(predict_results, actual_results)

    return result, accuracy

## A method to calculate the precision recall and accuracy for zeroR
def eval_zeroR(pridictList, tests, classifications):
    result = {}
    actual_results = []
    for t in tests:
        actual_results.append(t[-1])
    for c in classifications:
        if pridictList[0] == c:
            precision, recall = calculate(pridictList, actual_results, c)
            result[zeroR] = (precision, recall)
        else:
            result[c] = (0.0, 0.0)
    accuracy = cal_accuracy(pridictList, actual_results)
    return result, accuracy

# The detail method to calculate precision and recall for decision tree
def calculate(predict_results, actual_results, classification):
    precision = 0
    recall = 0
    tp = 0

    for i in range(len(predict_results)):
        if (predict_results[i] == actual_results[i]) and (predict_results[i] == classification):
            tp += 1

    tp_fp = predict_results.count(classification)
    tp_fn = actual_results.count(classification)

    if tp == 0:
        precision = 0
        recall = 0
    else:
        precision = tp / tp_fp
        recall = tp / tp_fn
    return precision, recall


# The detail method to calculate accuracy for decision tree and zeroR
def cal_accuracy(predict_results, actual_results):
    acc = 0
    for i in range(len(predict_results)):
        if (predict_results[i] == actual_results[i]):
            acc += 1
    accuracy = acc / len(predict_results)
    return accuracy

# A method to split data into n chunks so that can do n-fold validation.
def split_data(data, size):
    new_data = []
    splitsize = 1.0/size*len(data)
    for i in range(size):
        new_data.append(data[int(round(i*splitsize)):int(round((i+1)*splitsize))])
    return new_data


# The method to calculate average precision and recall for decision tree
def cal_average(total_accuracy, tests_result, n_fold, classifications):
    precision_recall = {}
    average_accuracy = total_accuracy/ n_fold

    print tests_result
    for c in classifications:
        total_precision = 0
        total_recall = 0
        for v in tests_result.values():
            total_precision += v.get(c)[0]
            total_recall += v.get(c)[1]
        precision_recall[c] = (total_precision / n_fold, total_recall / n_fold)

    return precision_recall, average_accuracy

# The method to calculate average precision and recall for zeroR
def cal_average_zeroR(total_accuracy, total_result, n_fold, classifications):
    precision_recall = {}
    average_accuracy = total_accuracy/ n_fold

    for c in classifications:
        total_precision = 0
        total_recall = 0
        count = 0
        for v in total_result.values():
            if v.get(c)[0] != 0:
                count += 1
            total_precision += v.get(c)[0]
            total_recall += v.get(c)[1]
        if count == 0:
            count = n_fold
        precision_recall[c] = (total_precision / count, total_recall / count)

    return precision_recall, average_accuracy

# A method to print every result of n-fold validations
def printDetail(time, result, accuracy):
    print 'result %s: %s'% (time, result)
    print 'accuracy: ', accuracy

# A method to print average precison, recall and accuracy for decision tree
def printResults(total_accuracy, result, n_fold, classifications):
    precision_recall, average_accuracy = cal_average(total_accuracy, result, n_fold, classifications)
    print 'average precision and average recall: ', precision_recall
    print 'average_accuracy:', average_accuracy
    print '\n'

# A method to print average precison, recall and accuracy for zeroR
def printResults_zeroR(total_accuracy, result, n_fold, classification):
    precision_recall, average_accuracy = cal_average_zeroR(total_accuracy, result, n_fold, classifications)
    print 'average precision and average recall: ', precision_recall
    print 'average_accuracy: ', average_accuracy
    print '\n'

if __name__ == '__main__' :
    r = readARFF
    d = decisionTree

    file = sys.argv[1]
    n_fold = int(sys.argv[2])
    if sys.argv[-1] == '-d':
        detail = True
    else:
        detail = False
    attrs, data = r.readArff(open(file))
    defaultValue = r.computeZeroR(attrs, data)

    time = 0
    tests_result = {}
    training_result = {}
    zeroR_result = {}
    zeroRList = []
    total_accuracy1 = 0
    total_accuracy2 = 0
    total_accuracy3 = 0
    average_accuracy = 0

    random.shuffle(data)
    new_data = split_data(data, n_fold)

    while time < n_fold:
        training = []
        tests = new_data[time]
        for index in range(len(new_data)):
            if index != time:
                training += new_data[index]
        time += 1
        tree = d.makeTree(training, attrs, defaultValue)
        classifications = set([item[-1] for item in training])

        result1, accuracy1 = eval_performance(tree, tests, classifications)
        total_accuracy1 += accuracy1
        tests_result[time] = result1

        result2, accuracy2 = eval_performance(tree, training, classifications)
        total_accuracy2 += accuracy2
        training_result[time] = result2


        zeroR = r.computeZeroR(attrs, tests)
        pridictList = [zeroR] * len(tests)
        result3, accuracy3 = eval_zeroR(pridictList, tests, classifications)
        total_accuracy3 += accuracy3
        zeroR_result[time] = result3

        if detail:
            print '%s tests result: %s' % (file, time)
            printDetail(time, result1, accuracy1)
            print '-------------------'
            print '%s training result: %s' % (file, time)
            printDetail(time, result2, accuracy2)
            print '-------------------'
            print '%s zeroR result: %s' % (file, time)
            printDetail(time, result3, accuracy3)
            print '============================================'

    print '%s tests results:' % file
    printResults(total_accuracy1, tests_result, n_fold, classifications)
    print '%s training results:' % file
    printResults(total_accuracy2, training_result, n_fold, classifications)
    print '%s zeroR results:' % file
    printResults_zeroR(total_accuracy3, zeroR_result, n_fold, classifications)






