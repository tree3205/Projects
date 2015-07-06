from __future__ import division
import cPickle as pickle
import sys
import string
import math
import operator
import os



__author__ = 'yxu66'

#Picke target
NEWS_GROUP = 'news_group'

# Stop word
STOPWORDS = ['a','able','about','across','after','all','almost','also','am','among',
             'an','and','any','are','as','at','be','because','been','but','by','can',
             'cannot','could','dear','did','do','does','either','else','ever','every',
             'for','from','get','got','had','has','have','he','her','hers','him','his',
             'how','however','i','if','in','into','is','it','its','just','least','let',
             'like','likely','may','me','might','most','must','my','neither','no','nor',
           'not','of','off','often','on','only','or','other','our','own','rather','said',
             'say','says','she','should','since','so','some','than','that','the','their',
             'them','then','there','these','they','this','tis','to','too','twas','us',
             'wants','was','we','were','what','when','where','which','while','who',
             'whom','why','will','with','would','yet','you','your']

## Global variable newsGroups: a dictory which the key is the category's name,
##  the value is a class named Category.
newsGroups = {}


## Class named Categoray is a dictionary that stores the category's name,
## each article in this category as a class,
## the df dictionary of this category
## and the TFIDF dictionary of this category.
class Category:

    def __init__(self, newsgroupDirectory):
        self.category = newsgroupDirectory.split('/')[-1]
        self.articles = []
        self.categoryIDF = {}
        self.categoryTFIDF = {}

    ## Class method: Given a directory of each newsgroup to build the df and idf dictionary of that newsgroup.
    def computeDocumentFrequency(self):
        appearedArticles = {}
        totalArticles = len(self.articles)
        for article in self.articles:
            for word in article.tf.keys():
                if not word in appearedArticles.keys():
                    appearedArticles[word] = 1
                else:
                    appearedArticles[word] += 1

        for word in appearedArticles.keys():
            tmp = totalArticles/appearedArticles[word]
            self.categoryIDF[word] = math.log(tmp, 2)

        return self.categoryIDF

    ## Class method: Use tf and idf to construct a dictionary of the 1000 highest-scoring words
    ## their TFIDF scores are summed over all articles in that category.
    def computeTFIDFCategory(self):
        tmpTFIDF = {}
        for article in self.articles:
            for w in article.articleTFIDF.keys():
                if not w in tmpTFIDF.keys():
                    tmpTFIDF[w] = article.articleTFIDF[w]
                else:
                    tmpTFIDF[w] += article.articleTFIDF[w]
        sorted_tfidf = sorted(tmpTFIDF.iteritems(), key=lambda x: (-x[1], x[0]))
        tfidf = sorted_tfidf[:1000]
        for i in range(len(tfidf)):
            self.categoryTFIDF[sorted_tfidf[i][0]] = sorted_tfidf[i][1]

## Representing an article, which stores the category (newsgroup name) of an article,
## a string containing its raw text,
## and a dictionary that maps words (except stopwords and non-words) to their TFIDF score.
class Article:

    def __init__(self, fileDirectory=None):
        self.category = ''
        self.newsgroupDirectory = ''
        self.rawText = ''
        self.tf = {}
        self.articleTFIDF = {}
        if fileDirectory:
            self.category = fileDirectory.split('/')[-2]
            try:
                file = open(fileDirectory)
                try:
                    self.rawText = file.read()
                    self.newsgroupDirectory = '/'.join(str for str in fileDirectory.split('/')[:-1])
                    self.computeTF()
                    self.articleTFIDF = {}
                except IOError:
                    print 'Cannot read the file.'
            except IOError:
                print 'Cannot find the file.'
            finally:
                file.close()

    ## Class method: Compute TF for each article.
    def computeTF(self):
        freq = {}
        totalWords = 0

        words = self.rawText.split()
        for word in words:
            if word in STOPWORDS:
                continue
            word = word.lower()
            word = stripPunctuation(word)
            if word.isalnum():
                totalWords += 1
                if word in freq.keys():
                    freq[word] += 1
                else:
                    freq[word] = 1

        for word in freq.keys():
            self.tf[word] = freq[word] / totalWords


    ## Class method: Compute TFIDF score for each word in an article by using the TF dictionary
    # and IDF dictionary.
    def computeTFIDF(self):
        idf = newsGroups[self.category].categoryIDF
        for word in self.tf.keys():
            self.articleTFIDF[word] = self.tf[word] * idf[word]

## Strip the punctuation at the beginning or the end of a word.
## Strip the punctuation between a word.
def stripPunctuation(str):
    punc = set(string.punctuation)
    for p in punc:
        if str.endswith(p)|str.startswith(p):
            str = ''.join(char for char in str if char not in punc)

    for p in punc:
        str = ''.join(s for s in str if s not in punc)

    return str

## Take as input two TFIDF dictionaries and return the cosine of the angle between their vectors.
def cosineSimilarity(tfidf1, tfidf2):
    similarity = 0
    intersection = 0
    v1Squares = 0
    v2Squares = 0
    for word1 in tfidf1.keys():
        v1 = tfidf1[word1]
        v1Squares += math.pow(tfidf1[word1], 2)
        for word2 in tfidf2.keys():
            if word2 == word1:
                v2 = tfidf2[word1]
                intersection += v1 * v2
            v2Squares += math.pow(tfidf2[word2], 2)
    tmp1 = math.sqrt(v1Squares)
    tmp2 = math.sqrt(v2Squares)
    similarity = intersection / (tmp1 * tmp2)

    return similarity

def classify(articlePath):
    similarity = 0
    identical = 0
    classification = ''
    tfidf1 = {}
    tmp = {}

    f = open('NEWS_GROUP', 'r')
    listTFIDF = pickle.load(f)
    f.close()
    a = Article(articlePath)
    for category in newsGroups.values():
        idf = category.categoryIDF
        tfidf2 = category.categoryTFIDF
        for t in a.tf.keys():
            for i in idf.keys():
                if t == i:
                    tfidf1[t] = a.tf[t] * idf[i]
        similarity = cosineSimilarity(tfidf1, tfidf2)

        if similarity > identical:
            identical = similarity
            classification = category.category

    return classification

def hCluster():
    similarity = 0
    identical = 0

    f = open('NEWS_GROUP', 'r')
    listTFIDF = pickle.load(f)
    f.close()
    while len(listTFIDF) > 1:
        compareSource = {}
        for i in range(len(listTFIDF)-1):
            tfidf1 = listTFIDF[i][1]
            for j in range((i + 1),len(listTFIDF)):
                tfidf2 = listTFIDF[j][1]
                similarity = cosineSimilarity(tfidf1, tfidf2)
                compareSource[similarity] = [listTFIDF[i], listTFIDF[j]]

        mostSimilar = max(compareSource.iteritems(), key=operator.itemgetter(0))[1]

        print '{ %s U %s }'% (mostSimilar[0][0], mostSimilar[1][0])
        newTFIDF = {}
        for w1 in mostSimilar[0][1].keys():
            for w2 in mostSimilar[1][1].keys():
                if w1 == w2:
                    newTFIDF[w1] = mostSimilar[0][1][w1] + mostSimilar[1][1][w1]
                else:
                    newTFIDF[w1] = mostSimilar[0][1][w1]
                    newTFIDF[w2] = mostSimilar[1][1][w2]
            tmpNewTFIDF = (mostSimilar[0][0]+'--'+ mostSimilar[1][0], newTFIDF)
        listTFIDF.remove(mostSimilar[0])
        listTFIDF.remove(mostSimilar[1])
        listTFIDF.append(tmpNewTFIDF)

if __name__ == '__main__' :
    listTFIDF = []
    tmp = ()
    d = '/Users/treexy1230/Documents/workspace/662/hw5/test1'
    for (parent, dirnames, filenames) in os.walk(d):
        for root in dirnames:
            if root.endswith('20_newsgroups'):
                continue
            c = Category(root)
            newsGroups[c.category] = c
        for file in filenames:
            if not file.endswith('.DS_Store'):
                path = os.path.join(parent, file)
                a = Article(path)
                newsGroups[a.category].articles.append(a)

    for c in newsGroups.values():
        categoryIDF = c.computeDocumentFrequency()
        for a in c.articles:
             a.computeTFIDF()
        c.computeTFIDFCategory()
        tmp = (c.category, c.categoryTFIDF)
        listTFIDF.append(tmp)
        f = open(NEWS_GROUP, 'wb')
        pickle.dump(listTFIDF, f)
        f.close()
    hCluster()



