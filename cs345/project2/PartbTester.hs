--yxu66
-----------------------------------
module Main where

import Partb
import Test.HUnit

--testing "set_add"
test1 = TestCase (assertEqual "set_add 2 (set_add 3 set_new)" 
								[2,3]
								(set_add 2 (set_add 3 set_new)))

--testing "set_remove"
test2 = TestCase (assertEqual "set_remove 3 (set_add 2 (set_add 3 set_new)" 
								[2]
								(set_remove 3 (set_add 2 (set_add 3 set_new))))

--testing "set_union"
test3 = TestCase (assertEqual "set_union (set_add 5 (set_add 2 (set_add 3 set_new)))  (set_add 1 (set_add 2 (set_add 6 set_new)))" 
								[1,2,3,5,6]
								(set_union (set_add 5 (set_add 2 (set_add 3 set_new)))  (set_add 1 (set_add 2 (set_add 6 set_new)))))  

--testing "set_intersect"
test4 = TestCase (assertEqual "set_intersect (set_add 5 (set_add 2 (set_add 3 set_new)))  (set_add 1 (set_add 2 (set_add 6 set_new)))" 
								[2]
								(set_intersect (set_add 5 (set_add 2 (set_add 3 set_new)))  (set_add 1 (set_add 2 (set_add 6 set_new))))) 

--testing "set_equal"
test5 = TestCase (assertEqual "set_equal (set_add 5 (set_add 2 (set_add 3 set_new)))  (set_add 1 (set_add 2 (set_add 6 set_new)))" 
								False
								(set_equal (set_add 5 (set_add 2 (set_add 3 set_new)))  (set_add 1 (set_add 2 (set_add 6 set_new))))) 


tests = TestList [TestLabel "test1" test1, 
						TestLabel "test2" test2,
						TestLabel "test3" test3,
						TestLabel "test4" test4,
						TestLabel "test5" test5]


main = do runTestTT tests
