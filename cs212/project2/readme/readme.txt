Name: YI XU
Student ID: 20269946
E-mail: yxu66@dons.usfca.edu

For my project2, I added three new classes to project1, that is StringParser class, SearchResultFile class and PartialSearch class. I also added a new method search to InvertedIndex class.

The StringParser class is used to deal with the String issue. When I wrote the PartialSearch class, I need to parse the line read in from query file to query words. I found that I could reuse the parseLineToStrArr method in InvertedIndexBuilder. But it doesn't make sense that I use a static method from InvertedIndexBuilder to parse the query words. So I move the static method to the new class.

The SearchResultFile class is used for sort the word search results according to the frequency and position. I made the class implements the Comparable interface, wrote a compareTo method to meet our requirements, and then I could add the SearchResultFile objects into a TreeSet to make them sorted.

The PartialSearch class is used to do the partial search. The static method partialSearch can read query words from a specific file and query each words with the search method in InvertedIndex class. After that, it will output the results to a specific file.

The search method in InvertedIndex class is used to do the partial search based on the invertedIndexMap. It will check the key set of invertedIndexMap to determine if there are some keywords start with the specific query words. If so, it retrieve the file paths and position lists and store them into a HashMap, whose key is the file path which contains the keywords meet the requirements and the value is another HashMap has two String keys, "frequency" and "position", just represent the meaning of their Integer values. With the HashMap, I can merge the file paths from different keywords which all meet our requirement and let the file paths only appear once in the result, get the sum frequency for one query word and find the earliest position of the query word in the file. After checking all query words, the method will traverse the HashMap and create SearchResultFile objects with the key-value pairs and put them into a TreeSet to make them sorted and return the TreeSet.

I think the most tricky part of the project is how to sort our search result according to the requirements. At first, I plan to deal with this problem like the method in homework04:Sorted Image Listing. However, when I really did this part. I found that in homework04, I create ImageLengthListing class to sort the input File objects, but here I have to create my own "File class"(the SearchResultFile). When I googled online for information of homework04, I also found people used the objects of a class which implements Comparable interface and TreeSet to make objects sorted. I thought maybe that is more similar to the case of project2. So I just finish the sort part as this way.

