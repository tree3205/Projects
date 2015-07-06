from __future__ import division

__author__ = 'yxu66'

import os
import string
import sys
import random

NEGATIONWORDS = ["won't", "wouldn't", "shan't", "shouldn't", "can't", "cannot", "couldn't", "mustn't",
                "isn't", "aren't", "wasn't", "weren't", "hasn't", "haven't", "hadn't", "doesn't", "don't",
                "didn't","not", "no", "never"]

DELIMITERS = [',', '.', ':', '-']

## A method to build the lexicon of positive words and negative words.
def buildLexicon(instr):
    lexicon = {}
    key = ''
    valueList = []
    str = instr.split('\n')

    for s in str:
        if s.startswith('; Opinion'):
            key = s[-8:]
            continue
        elif s.startswith(';'):
            continue
        elif s == '':
            continue
        else:
            valueList.append(s)
        lexicon[key] = valueList

    return lexicon

## A method to count the number of positive and negative sentiment words,
# and classify the sentence as positive if there are more positive sentiment words, negative otherwise.
def calculateNormal(inputStr, lexicon, file):
    results = {}
    valueList = []
    count_cons = 0
    count_pros = 0
    lines = [line.lower() for line in inputStr.readlines() if not line.startswith('%') and len(line) > 1]
    total = len(lines)

    for str in lines:
        cons = 0
        pros = 0
        decideValue = ''
        tmpList = str.split()
        for word in tmpList:
            if word.startswith('<'):
                word = word[6:]
            elif word.endswith('>'):
                word = word[:-7]
            word = word.strip(string.punctuation)
            value = search(word, lexicon)
            if value == 'Negative':
                cons += 1
            elif value == 'Positive':
                pros += 1
        if cons >= pros:
            decideValue = 'Negative'
        else:
            decideValue = 'Positive'
        valueList.append(decideValue)
    count_pros = valueList.count('Positive')
    count_cons = valueList.count('Negative')
    results[file] = [count_pros/total, count_cons/total]

    return results

def caculateNegation(inputStr, lexicon, file):
    results = {}
    valueList = []
    count_cons = 0
    count_pros = 0
    negate = False
    endFlag = False
    lines = [line.lower() for line in inputStr.readlines() if not line.startswith('%') and len(line) > 1]
    total = len(lines)

    for str in lines:
        cons = 0
        pros = 0
        decideValue = ''
        tmpList = str.split()
        for word in tmpList:
            if word.startswith('<'):
                word = word[6:]
            if word.endswith('>'):
                word = word[:-7]
            if negate == True:
                for d in DELIMITERS:
                    if word.endswith(d):
                        endFlag = True

            word = word.strip(string.punctuation)
            if negate == True:
                value = negation(word, lexicon)
            else:
                value = search(word, lexicon)

            if value == 'Negative':
                cons += 1
            elif value == 'Positive':
                pros += 1

            if word in NEGATIONWORDS:
                negate = True
            if endFlag:
                endFlag = False
                negate = False
        if cons >= pros:
            decideValue = 'Negative'
        else:
            decideValue = 'Positive'
        valueList.append(decideValue)
    count_pros = valueList.count('Positive')
    count_cons = valueList.count('Negative')
    results[file] = [count_pros/total, count_cons/total]

    return results

## A method to negate the value of current word.
def negation(word, lexicon):
    negateValue = ''
    value = search(word, lexicon)
    if value == 'Negative':
        negateValue = 'Positive'
    elif value == 'Positive':
        negateValue = 'Negative'

    return  negateValue

## A method to search the word in lexicon, return whether it is negative or positive word.
def search(word, lexicon):
    value = ''
    for item in lexicon:
        for k, v in item.items():
            if word in v:
                value = k
            else:
                continue

    return value

def printResult(result):
    for item in result:
        name = item.keys()[0]
        percentageOfPros = item.values()[0][0]
        percentageOfCons = item.values()[0][1]
        print '{0}: pros -> {1:.3f}, cons -> {2:.3f}'.format(name, percentageOfPros, percentageOfCons)

if __name__ == '__main__' :
    lexicon_dir = '/Users/treexy1230/Documents/workspace/662/hw7/opinion-lexicon-English'
    files_dir = '/Users/treexy1230/Documents/workspace/662/hw7/pros-cons'
    lexicon = []
    r1 = []
    r2 = []
    for (parent, dirnames, filenames) in os.walk(lexicon_dir):
        for file in filenames:
            if not file.endswith('.DS_Store'):
                path = os.path.join(parent, file)
                tmp = open(path)
                try:
                    instr = tmp.read()
                    result = buildLexicon(instr)
                    lexicon.append(result)
                except IOError:
                    print 'Cannot find the file.'
                finally:
                    tmp.close()

    for (parent, dirnames, filenames) in os.walk(files_dir):
        for file in filenames:
            if not file.endswith('.DS_Store'):
                path = os.path.join(parent, file)
                inputStr1 = open(path)
                inputStr2 = open(path)
                try:
                    results1 = calculateNormal(inputStr1, lexicon, file)
                    r1.append(results1)
                    result2 = caculateNegation(inputStr2, lexicon, file)
                    r2.append(result2)
                except IOError:
                    print 'Cannot find the file.'
                finally:
                    inputStr1.close()
                    inputStr2.close()


    print 'The accuracy without negation: '
    printResult(r1)
    print 'The accuracy with negation: '
    printResult(r2)



    # inputStr = open('test.txt')
    # r = calculateNormal(inputStr, lexicon, 'test.txt')
    # print r
