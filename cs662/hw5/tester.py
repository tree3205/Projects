import os, random, sys, re
import cPickle as pickle
import article

if __name__ == '__main__' :
    path = ''
    correct = 0
    incorrect = 0
    dir = '/Users/treexy1230/Documents/workspace/662/hw5/test1'
    for (parent, dirnames, filenames) in os.walk(dir):
        for root in dirnames:
            if root.endswith('20_newsgroups'):
                continue
            c = article.Category(root)
            article.newsGroups[c.category] = c
        for file in filenames:
            if not file.endswith('.DS_Store'):
                path = os.path.join(parent, file)
                a = article.Article(path)
                print 'category', a.category
                article.newsGroups[a.category].articles.append(a)

    for c in article.newsGroups.values():
        categoryIDF = c.computeDocumentFrequency()
        for a in c.articles:
            a.computeTFIDF()
        c.computeTFIDFCategory()

    num = int(sys.argv[1])
    for i in range(num):
        files = [os.path.join(path, filename) for path, dirs, files in os.walk(dir) for filename in files if not filename.endswith(".DS_Store")]
        path = random.choice(files)
        print path
        correctCategory = path.split('/')[-2]
        ourResult = article.classify(path)
        if ourResult == correctCategory:
            correct += 1
        else:
            incorrect += 1
        print 'Try %s, Correct: %s' % ((i+1), correct)
        print 'Correct category: %s, our Result: %s' % (correctCategory, ourResult)
        print '=================='
        i += 1

    print '!!!!!Try %s, Correct: %s' % (i, correct)