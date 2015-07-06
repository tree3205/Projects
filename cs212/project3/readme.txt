Name: YI XU
Student ID: 20269946
E-mail: yxu66@dons.usfca.edu

For my project3, I added two new classes: WorkQueue and MultiReaderLock. I also modified InvertedIndex to make it multi-thread safe, and modified InvertedIndexBuilder and PartialSearch to make them to be multi-thread version.

WorkQueue is based on IBM developerWorks Work Queue(http://www.ibm.com/developerworks/library/j-jtp0730/index.html) and course code from CS212(http://cs212.cs.usfca.edu/lectures/multithreading/WorkQueue.java?attredirects=0&d=1). I remove the nThreads attribute from IBM WorkQueue and use standard output to replace log4j message because I don't plan to use log4j in this project.

MultiReaderLock is a multi-reader and single-writer lock. I use two Integer to represent the current number of readers and writers on this lock. If there existed any readers or writers, then the write lock cannot be required. If there existed a writer, then the read lock cannot be required.

In the InvertedIndex, I remove the addRecord method but add an addFile method. And the addFile method will create a sub result for the file, so that it will only need to require write lock when it starts to write the sub result to invertedIndexMap. And now the outputToFile and search method will require a read lock when it get access to invertedIndexMap.

For the InvertedIndexBuilder and PartialSearch, I did similar modification as the DirectoryListBuilder of homework07. I call the shutdown function when the pending number go back to 0, which indicate that the work is done, so that my project can quit normally without call system.exit().

At first, I use a same workqueue in the Driver and pass it to InvertedIndexBuilder and PartialSearch. However, I found that actually PartialSearch will always work after InvertedIndexBuilder and the two guys will never share the same workqueue. So I decide to use different workqueue for these two classes, because I think the two different task type have different time cost. In the future, maybe I need to customized the number of threads when do these two type tasks. For example, I might use 10 threads to do partial search but only 5 to do invertedIndex build, because we only allow one thread to write, but all of the search workers can work at the same time. 

