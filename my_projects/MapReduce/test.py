import unittest

from mr_master import Master
from mr_worker import Worker


class MrTest(unittest.TestCase):
    # Master test
    def test_split_single_file1(self):
        mr = Master('3205', '/Users/WofloW/USF/CS636/project02')
        split_info = mr.split_file(3, "test_data")
        self.assertEqual(split_info, ({0: [("/Users/WofloW/USF/CS636/project02/test_data", 0, 3)],
                                       1: [("/Users/WofloW/USF/CS636/project02/test_data", 3, 6)],
                                       2: [("/Users/WofloW/USF/CS636/project02/test_data", 6, 7)]},
                                      [('/Users/WofloW/USF/CS636/project02/test_data', 7)]))

    def test_split_single_file2(self):
        mr = Master('3205', '/Users/WofloW/USF/CS636/project02')
        split_info = mr.split_file(4, "test_data")
        self.assertEqual(split_info, ({0: [("/Users/WofloW/USF/CS636/project02/test_data", 0, 4)],
                                       1: [("/Users/WofloW/USF/CS636/project02/test_data", 4, 7)]},
                                      [('/Users/WofloW/USF/CS636/project02/test_data', 7)]))

    def test_multiple_split1(self):
        mr = Master('3205', '/Users/WofloW/USF/CS636/project02')
        split_info = mr.split_file(6, "test_data_")
        self.assertEqual(split_info, ({0: [("/Users/WofloW/USF/CS636/project02/test_data_0", 0, 6)],
                                       1: [("/Users/WofloW/USF/CS636/project02/test_data_1", 0, 6)]},
                                      [('/Users/WofloW/USF/CS636/project02/test_data_0', 6),
                                       ('/Users/WofloW/USF/CS636/project02/test_data_1', 6)]))

    def test_multiple_split2(self):
        mr = Master('3205', '/Users/WofloW/USF/CS636/project02')
        split_info = mr.split_file(4, "test_data_")
        self.assertEqual(split_info, ({0: [("/Users/WofloW/USF/CS636/project02/test_data_0", 0, 4)],
                                       1: [("/Users/WofloW/USF/CS636/project02/test_data_0", 4, 6),
                                           ("/Users/WofloW/USF/CS636/project02/test_data_1", 0, 2)],
                                       2: [("/Users/WofloW/USF/CS636/project02/test_data_1", 2, 6)]},
                                      [('/Users/WofloW/USF/CS636/project02/test_data_0', 6),
                                       ('/Users/WofloW/USF/CS636/project02/test_data_1', 6)]))


    def test_multiple_split3(self):
        mr = Master('3205', '/Users/WofloW/USF/CS636/project02')
        split_info = mr.split_file(7, "test_data_")
        self.assertEqual(split_info, ({0: [("/Users/WofloW/USF/CS636/project02/test_data_0", 0, 6),
                                           ("/Users/WofloW/USF/CS636/project02/test_data_1", 0, 1)],
                                       1: [("/Users/WofloW/USF/CS636/project02/test_data_1", 1, 6)]},
                                      [('/Users/WofloW/USF/CS636/project02/test_data_0', 6),
                                       ('/Users/WofloW/USF/CS636/project02/test_data_1', 6)]))

    # Worker test
    def test_read_map_task(self):
        mr = Worker("127.0.0.1:3205", "127.0.0.1:3207")
        f = open("word_count.py")
        script = f.read()
        mr.read_map_task(["word_count.py", script], 0, 1, 3, "task_name", 4)
        data = mr.read_data_from_file("/Users/WofloW/USF/CS636/project02/test_data_0", 1, 3)
        self.assertEqual(data, "bcd")

    def test_read_input(self):
        mr = Worker("127.0.0.1:3205", "127.0.0.1:3207")
        f = open("word_count.py")
        script = f.read()
        mr.read_map_task(["word_count.py", script], 0, 2, 3, "task_name", 7)
        mr.data_dir = "/Users/WofloW/USF/CS636/project02/"
        data = mr.read_input([("/Users/WofloW/USF/CS636/project02/test_data_0", 0, 6),
                              ("/Users/WofloW/USF/CS636/project02/test_data_1", 0, 1)], 0, 2,
                             [('/Users/WofloW/USF/CS636/project02/test_data_0', 6),
                              ('/Users/WofloW/USF/CS636/project02/test_data_1', 6)])
        self.assertEqual(data, "abcdefghijkl")

    # test unit = 1
    def test_read_input2(self):
        mr = Worker("127.0.0.1:3205", "127.0.0.1:3207")
        f = open("word_count.py")
        script = f.read()
        mr.read_map_task(["word_count.py", script], 0, 2, 3, "task_name", 7)
        mr.data_dir = "/Users/WofloW/USF/CS636/project02/"
        mr.unit = 1
        data = mr.read_input([("/Users/WofloW/USF/CS636/project02/test_data_0", 0, 6),
                              ("/Users/WofloW/USF/CS636/project02/test_data_1", 0, 1)], 0, 2,
                             [('/Users/WofloW/USF/CS636/project02/test_data_0', 6),
                              ('/Users/WofloW/USF/CS636/project02/test_data_1', 6)])
        self.assertEqual(data, "abcdefg")

    # test unit = 2
    #def read_input(self, split_info, mapper_id, num_mapper, file_info):
    def test_read_input3(self):
        mr = Worker("127.0.0.1:3205", "127.0.0.1:3207")
        f = open("word_count.py")
        script = f.read()
        mr.read_map_task(["word_count.py", script], 0, 2, 3, "task_name", 7)
        mr.data_dir = "/Users/WofloW/USF/CS636/project02/"
        mr.unit = 2
        data = mr.read_input([("/Users/WofloW/USF/CS636/project02/test_data_0", 0, 6),
                                ("/Users/WofloW/USF/CS636/project02/test_data_1", 0, 1)], 0, 2,
                             [('/Users/WofloW/USF/CS636/project02/test_data_0', 6),
                              ('/Users/WofloW/USF/CS636/project02/test_data_1', 6)])
        self.assertEqual(data, "abcdefgh")

    #def read_input(self, split_info, mapper_id, num_mapper, file_info):
    def test_read_input4(self):
        mr = Worker("127.0.0.1:3205", "127.0.0.1:3207")
        f = open("word_count.py")
        script = f.read()
        mr.read_map_task(["word_count.py", script], 1, 2, 3, "task_name", 7)
        mr.data_dir = "/Users/WofloW/USF/CS636/project02/"
        mr.unit = 2
        data = mr.read_input([("/Users/WofloW/USF/CS636/project02/test_data_1", 1, 6)],
                                1, 2,
                             [('/Users/WofloW/USF/CS636/project02/test_data_0', 6),
                              ('/Users/WofloW/USF/CS636/project02/test_data_1', 6)])
        self.assertEqual(data, "ijkl")



    # mr_seq test

if __name__ == '__main__':
    unittest.main()