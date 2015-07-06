--module Main where

--import Control.Monad
--import System.IO
--import Network.HTTP

import System.Environment
import System.Exit
 
    main = getArgs >>= parse >>= putStr . tac
 
    tac  = unlines . reverse . lines
 
    parse ["-h"] = usage   >> exit
    parse ["-v"] = version >> exit
    parse []     = getContents
    parse fs     = concat `fmap` mapM readFile fs
 
    usage   = putStrLn "Usage: tac [-vh] [file ..]"
    version = putStrLn "Haskell tac 0.1"
    exit    = exitWith ExitSuccess
    die     = exitWith (ExitFailure 1)



{-main = do
	myRequest = getRequest "http://duckduckgo.com"
	--simpleHTTP myRequest >>= getResponseBody
	theInput <- simpleHTTP myRequest-}


{-main = do
	theInput <- readFile "Readme.txt"
	writeFile "output.txt" (reverse theInput)-}

{-main  = do
	putStrLn "Want to quit? y/n"
    ans <- getLine
    if ans /= "y" then do
       putStrLn "Not quit"
       main
    else return ()-}

{-main = do
	putStrLn "quit the program? y/n"
	ans <- getLine
  	if ans /= "y" then do
    	putStrLn "not quitting"
    	main
  	else return ()-}

{-main = do
	putStrLn "quit? y/n"
	ans <- getLine
	when (ans /= "y") $ do
		putStrLn "not quit"
		main-}

