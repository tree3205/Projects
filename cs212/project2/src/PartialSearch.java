//:object/PartialSearch.java

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.TreeSet;
import java.util.ArrayList;

/**
 * Do partial search with provided InvertedIndex map.
 * 
 * @author yxu66
 */
public class PartialSearch 
{
	
	/**
	 * Read query words list from a specific file and do partial search based on
	 * the provided words map. Then output the results to specific file.
	 * 
	 * @param queryFile the file contains query words
	 * @param datamap the provided InvertedIndex map
	 * @param outputFilePath the path of the output file.
	 */
	public static void partialSearch(File queryFile, InvertedIndex dataMap, String outputFilePath)
	{
		TreeSet<SearchResultFile> searchResultSet;
		BufferedReader in;
		try {
			PrintWriter out = new PrintWriter(outputFilePath);
			try 
			{
				in = new BufferedReader(new FileReader(queryFile));
				String line;
				while ((line = in.readLine()) != null)
				{
					ArrayList<String> words = StringParser.parseLineToStrArr(line);
					searchResultSet = dataMap.search(words);
					out.println(line);
					for (SearchResultFile f : searchResultSet)
					{
						out.println('"' + f.getFilePath() + '"' + ", "  +  f.getFrequency() + ", " + f.getPosition());
					}
					out.println();
				}
				in.close();
				out.close();
			} 
			catch (IOException e) 
			{
				System.out.println("Errors occured when reading query words.");
			}
		}
		catch (FileNotFoundException e1) 
		{
			System.out.println("Cannot open or create the output file.");
		}
	}
}
