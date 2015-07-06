import Data.List

set_new :: [a]
set_new = []

set_add :: (Eq a) => a -> [a] -> [a]
set_add e [] = [e]
set_add e (x:xs) = if  e == x then e : xs else x : set_add e xs



set_add1 :: (Eq a) => a -> [a] -> [a]
set_add1 e [] = [e]
set_add1 e (x:xs) | e == x    = e : xs 
				  | otherwise = x : set_add e xs


set_add2 :: (Eq a) => a -> [a] -> [a]
set_add2 e xs = [x | x <- xs, e /= x] ++ [e]

