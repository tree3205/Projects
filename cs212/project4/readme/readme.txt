Name:YI XU
Id:yxu66
Email:yxu66@dons.usfca.edu

I reuse my HTMLLinkParser in homework08. But I modified a bit that now HTMLLinkParser will find all a tags from the given text. Then it find all links in these a tags and check if these links were mailto link. So that we could make sure all of the links are valid link.

Then I use InvertedIndexBuilder as the model, plus the HTMLCleaner in homework09 to create my InvertedIndexHTMLBuilder. I use a private ArrayList<URL> to store the crawled urls, so that I could tell if a new coming url is needed to parsed. Because ArrayList is not thread-safe, I use a MultiReaderLock object to make it thread-safe. I use a private int as the page numbers count. I made the count plus one when a url passed the identical check. When finding new urls, I will check if the pages count arrived the maximum threshold. During parsing a web page, I will check the header to tell if it is text/html type.