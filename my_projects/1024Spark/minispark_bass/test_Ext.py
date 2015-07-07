from ms_SparkContext import *
from datetime import datetime

def do(spark):
    start_time = datetime.now()
    data1 = spark.textFile("sort.txt", 4)
    words1 = data1.flatMap(lambda line: line.split(" "))
    print spark.topByKey(words1, 5)
    end_time = datetime.now()
    print('Duration of TopByKey: {}'.format(end_time - start_time))
    #
    #

    filepath = "/Users/WofloW/project/test/file_1M.txt"
    start_time = datetime.now()
    data2 = spark.textFile(filepath, 4)
    words2 = data2.flatMap(lambda line: line.split(" "))
    result1= spark.topByKey(words2, 10)
    end_time = datetime.now()
    duration1 = end_time - start_time

    start_time = datetime.now()
    data2 = spark.textFile(filepath, 4)
    words2 = data2.flatMap(lambda line: line.split(" "))
    result2 = sorted(spark.collect(words2))[:10]

    end_time = datetime.now()
    duration2 = end_time - start_time

    start_time = datetime.now()
    data1 = spark.textFile(filepath, 4)
    words1 = data1.flatMap(lambda line: line.split(" "))
    sorted = words1.sortByKey()
    result3 = spark.collect(sorted)[:10]
    end_time = datetime.now()
    duration3 = end_time - start_time



    print("****************************")
    print('Duration of TopByKey: {}'.format(duration1))
    print result1
    print
    print("****************************")
    print('Duration of Collect Sort: {}'.format(duration2))
    print result2
    print
    print("****************************")
    print('Duration of Partial Sort: {}'.format(duration3))
    print result3
    print



    subprocess.call(["sh", "stopall.sh"])


