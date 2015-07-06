--yxu66
-----------------------------------
module Main where

import Parta
import Test.HUnit

-- testing "church_succ"
test1 = TestCase (assertEqual "unchurch $  church_succ (church 2)" 
										3
										(unchurch $ church_succ (church 2)))

-- testing "church_plus"
test2 = TestCase (assertEqual "unchurch $  church_plus (church 2) (church 6)" 
										8
										(unchurch $ church_plus (church 2) (church 6)))

-- testing "church_mult"									
test3 = TestCase (assertEqual "unchurch $ church_mult (church 2) (church 4)" 
										8
										(unchurch $ church_mult (church 2) (church 4)))

-- testing "church_exp"	
test4 = TestCase (assertEqual "unchurch $ church_exp (church 2) (church 4)" 
										16
										(unchurch $ church_exp (church 2) (church 4)))

-- testing "church_pred"
test5 = TestCase (assertEqual "unchurch $  church_pred (church 2)" 
										1
										(unchurch $ church_pred (church 2)))

-- testing "church_zerop"
test6 = TestCase (assertEqual "church_zerop (church 2)" 
										False
										(church_zerop (church 2)))

-- testing "factorial"
test7 = TestCase (assertEqual "unchurch $  factorial (church 4)" 
										24
										(unchurch $ factorial (church 4)))
								
								
tests = TestList [TestLabel "test1" test1, 
						TestLabel "test2" test2,
						TestLabel "test3" test3,
						TestLabel "test4" test4,
						TestLabel "test5" test5,
						TestLabel "test6" test6,
						TestLabel "test7" test7]


main = do runTestTT tests
