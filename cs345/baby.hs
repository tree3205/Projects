doubleMe x = x + x
doubleUs x y = x + x + y + y

rev [] = []
rev xs = rev (tail xs) ++ [head xs]

zipTogether :: [a] -> [b] -> [(a,b)]
zipTogether [] [] = []
zipTogether (x:xs) (y:ys) = (x,y) : zipTogether xs ys


type Church a = (a -> a) -> (a -> a)
 
church :: Integer -> Church Integer
church 0 = \f -> \x -> x
church n = \f -> \x -> f (church (n-1) f x)

unchurch :: Church Integer -> Integer
unchurch n = n (\x -> x + 1) 0

church_succ :: Church Integer -> Church Integer
church_succ = \n f x -> f (n f x)