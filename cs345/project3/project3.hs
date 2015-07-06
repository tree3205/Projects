import System.Environment (getArgs)
import System.IO
import Data.List
import Text.Regex.Posix
import qualified Data.Map as Map
import Control.DeepSeq (rnf)

--Get flag
parse_bycount :: [String] -> Bool
parse_bycount args = if "-bycount" `elem` args then True else False

--------------------------------------

--Get file name
get_htmls :: [String] -> [String]
get_htmls args = [x | x <- args, x /= "-bycount"]

--------------------------------------

--Get text
get_text :: [String] -> IO [String]
get_text args = mapM myread htmls
	where htmls = [x | x <- args, x /= "-bycount"]

myread :: String ->  IO String
myread s = do
	h <- openFile s ReadMode
	hSetEncoding h char8
	hSetEncoding stdout char8
	input <- hGetContents h
	return input


--------------------------------------
--Findlinks: two ways to implement
find_links1 :: [String] -> [String]
find_links1 text = concat links 
		where links = anchorLinks =~ pat :: [[String]]
		      pat = "https?[^>^\\^\"^\'^ ]+"
		      --may have problem
		      anchorLinks = unwords $ (map concat roughText)
		      roughText = map search_anchor text

search_anchor :: String -> [String]
search_anchor content 
	| hasNext content = result : search_anchor after
	| otherwise = []
	where  (before, result, after) = get_content content
	       get_content content = content =~ pattern :: (String,String,String)
	       hasNext content = content =~ pattern :: Bool
	       pattern = "<[aA][^>]*[Hh][Rr][Ee][Ff][ \\s]*=[ \\s]*[\"\']https?[^>^\\^\"^\']+[\"\']"
	       --may have problem


find_links :: [String] -> [String]
find_links text = concat $ links 
	    where  links = anchorLinks =~ pat :: [[String]]
	    	   pat = "https?[^>^\\^\"\'^ ]+"
	    	   anchorLinks =unwords $ concat $ concat $ map search_links text
		   search_links content = content =~ pattern :: [[String]] 
		   pattern  = "<[aA][^>]*[Hh][Rr][Ee][Ff][ \\s]*=[ \\s]*[\"\']https?[^>^\\^\"^\']+[\"\']"
--------------------------------------

--Creade tuple list [(Links, count)]
links_data :: [String] -> [(String, Int)]
links_data linksList =  map (\l@(x:xs) -> (x,length l)) . group . sort $ linksList

--------------------------------------

--Sorting by name or count

sort_links :: [String] -> [(String, Int)] -> [(String, Int)]
sort_links args dataList
	| hasBycount args = sort_bycount dataList
	| otherwise = sort_byname dataList
		where  hasBycount args = if "-bycount" `elem` args then True else False	
               
sort_byname :: [(String, Int)] -> [(String, Int)]
sort_byname dataList = Map.assocs $ Map.fromList dataList


sort_bycount :: [(String, Int)] -> [(String, Int)]
sort_bycount dataList = concat $ int_toString bycountList
	where  bycountList = merge_samecount countList
	       countList = string_toInt dataList


string_toInt :: [(String, Int)] -> [(Int, [String])]
string_toInt [] = []
string_toInt (x:xs) =  (snd x, [fst x]) : string_toInt xs


merge_samecount :: (Ord k) => [(k, [String])] -> [(k, [String])]
merge_samecount countList =  Map.toDescList $ Map.fromListWith (++) countList


int_toString :: [(Int, [String])] -> [[(String, Int)]]
int_toString [] = []
int_toString (x:xs) =[(a, fst x) | a <- sort $ snd x] : int_toString xs

-------------------------------------

--Print result

show_tuple :: (String, Int) -> String
show_tuple (links, count) = "\"" ++ links ++ "\"" ++"," ++ (show count) ++ "\n"
    
show_tuple_list :: [(String, Int)] -> String
show_tuple_list = (foldl' (++) "") . (map show_tuple)
--------------------------------------
--------------------------------------

main = do
	args <- getArgs
	text <- get_text args

	linksList <- return $ find_links text
	dataList <- return $ links_data linksList
	result <- return $ sort_links args dataList

	outcome <- return $ show_tuple_list result

	putStr outcome

        

	
	





