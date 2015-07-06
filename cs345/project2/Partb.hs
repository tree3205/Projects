--yxu66
-----------------------------------
module Partb 
( set_new 
, set_add 
, set_remove 
, set_union  
, set_intersect  
, set_equal
) where 
-----------------------------------

set_new :: [a]
set_new = []

set_add :: (Ord a) => a -> [a] -> [a]
set_add e [] = [e]
set_add e s = [x | x <- s, x < e] ++ [e] ++ [x | x <- s, x > e]

set_remove :: (Ord a) => a -> [a] -> [a]
set_remove e [] = []
set_remove e s = [x | x <- s, x /= e]

set_union :: (Ord a) => [a] -> [a] -> [a]
set_union [] s2 = s2
set_union  s1 [] = s1
set_union (x:xs) s2 = if x `elem` s2 then set_union xs s2 else set_union xs (set_add x s2)

set_intersect :: (Ord a) => [a] -> [a] -> [a]
set_intersect s1 [] = []
set_intersect [] s2 = []
set_intersect s1 s2 = [x| x <- s1, y <- s2, x == y]

set_equal:: (Ord a) => [a] -> [a] -> Bool
set_equal [] [] = True
set_equal (x: xs) (y: ys) = if x == y then set_equal xs ys else False

