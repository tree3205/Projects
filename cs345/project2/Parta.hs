--yxu66
{-# LANGUAGE RankNTypes #-}

module Parta 
( church  
, unchurch  
, church_succ 
, church_plus  
, church_mult   
, church_exp
, church_pred 
, church_zerop
, factorial
) where 

-----------------------------------


type Church = forall a. (a -> a) -> (a -> a)
 
church :: Integer -> Church 
church 0 = \f -> \x -> x
church n = \f -> \x -> f (church (n-1) f x)

unchurch :: Church -> Integer
unchurch n = n (\x -> x + 1) 0

church_succ :: Church -> Church 
church_succ = \n -> \f -> \x -> f (n f x)

church_plus :: Church -> Church -> Church 
church_plus = \m -> \n -> \f -> \x -> m f (n f x)

church_mult :: Church -> Church -> Church 
church_mult = \m -> \n -> \f -> m (n f)

church_exp :: Church -> Church -> Church
church_exp = \m -> \n -> n m

church_pred :: Church -> Church
church_pred = \n f x -> n (\g h -> h (g f)) (\u -> x) (\u -> u) 

--church_sub:: Church -> Church -> Church
--church_sub = \m -> \n -> n church_pred m

church_zerop :: Church -> Bool
church_zerop m = if unchurch m == 0 then True else False

factorial :: Church -> Church
factorial n = if church_zerop n then church 1 else church_mult n (factorial (church_pred n))



 