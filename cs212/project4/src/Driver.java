//:object/Driver.java

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Test the project4
 * 
 * @author yxu66
 */
public class Driver 
{

	/**
	 * Build an invertedIndexMap based on given seed URL, do partial search on the
	 * invertedIndexMap and output the result to a certain file.
	 * Output the specific invertedIndexMap of provided seed URL to a certain file if required.
	 * 
	 * @param args the argument that input to test the program.
	 */
	public static void main(String[] args) 
	{	
		ArgumentParser argParser = new ArgumentParser(args);
		if (argParser.hasFlag("-u") && argParser.hasFlag("-q") && argParser.hasFlag("-t"))
		{
			String rootURL = argParser.getValue("-u");
			URL root;
			try 
			{
				root = new URL(rootURL);

				String queryFilePath = argParser.getValue("-q");
				File queryFile = new File(queryFilePath);

				int threads = Integer.parseInt(argParser.getValue("-t"));
				
				InvertedIndex wordsMap = new InvertedIndex();
				
				InvertedIndexHTMLBuilder mapBuilder = new InvertedIndexHTMLBuilder(wordsMap, threads, 50);
				mapBuilder.parseURL(root);
				
				PartialSearch partialSearcher = new PartialSearch(wordsMap, threads);
				partialSearcher.partialSearch(queryFile, "searchresults.txt");
				
				if (argParser.hasFlag("-i"))
				{
					wordsMap.outputToFile("invertedindex.txt");
				}
			}
			catch (MalformedURLException e)
			{
				System.out.println("The URL is malformed.");
			}
			catch (Exception e)
			{
				System.out.println("<threads> should be an Integer.");
			}
			}
			else
			{
				System.out.println("Usage: java -cp project4.jar Driver -u <seed> -q <queryfile> -t <threads> [-i]");
			}
	}

}
