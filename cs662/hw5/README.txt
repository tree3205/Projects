README

Yxu66

The overal data structure of my project is just like this:
										 1)category(the article stays in)
				 1.category(category name)                      /2)newsgroupDirectory(the path of
			       / 2.articles([class article1, class article2…])  -  folder that contains article	
20_newsgroup--->Class Category - 3.categoryIDF					\3)rawText
			       \ 4.categoryTFIDF				 4)tf of article
										 5)articleTFIDF


1.when dealing with the raw text by to extend this to hyphens within words, for example:cantaloupe.srv.cs.cmu.edu will be changed to cantaloupesrvcscmuedu. And set the list stop word to strip them. Also I choose isalnum() method to strip non word.

2. computeDocumentFrequency:Set up the idf for each category by using the tf dictionary once get the article. So we do not need to strip the non word and stop word again, which will be more efficient.

3. computeTFIDF: Compute the tfidf scores for each word in articles by idf of category from 2.

4. computeTFIDFCategory: Compute the tfidf scores for each category by compute the tf dictionary for each category and then choose 1000 highest-scoring words as this category's tfidf dictionary.

5.cosineSimilarity: Use two tfidf dictionary, one comes from article and another comes from category to compute the similarity.

6.classify: Given an article, generate its tf dictionary, and try different idc dictionary of each category get its
tfidf dictionary, along with this category's tfidf dictionary to find the classification.

7.hCluster: the data structure to get the clustering (dendrogram) corresponding to the 20 newsgroup data set and write a method that displays it. For convenience, I chose to pickle a list [[category1, categoryTFIDF1],[category2, categoryTFIDF2]…].Choose any two of them to calculate the similarity and merge the two have the highest score, and then delete the original two and add the new merged one to continue to calculate the similarity and so on until there is only one left.


I tried hard to improve the accuracy of the classification, but it is fast and almost 90% above correct to deal with small corpus from 20-newsgroup, but when I run the whole newsgroup, it was very slow and the accuracy will drop to about 50%. 