//:object/Driver.java

import java.io.File;

/**
 * Test the project4
 * 
 * @author yxu66
 */
public class Driver 
{

	/**
	 * Output the specific invertedIndexMap of provided seed URL to a certain file.
	 * 
	 * @param args the argument that input to test the program.
	 */
	public static void main(String[] args) 
	{

		ArgumentParser argParser = new ArgumentParser(args);
		if (argParser.hasFlag("-d") && argParser.hasFlag("-q"))
		{
			String rootPath = argParser.getValue("-d");
			File root = new File(rootPath);
			String queryFilePath = argParser.getValue("-q");
			File queryFile = new File(queryFilePath);
			InvertedIndex wordsMap = new InvertedIndex();
			InvertedIndexBuilder.buildMap(wordsMap, root);
			PartialSearch.partialSearch(queryFile, wordsMap, "searchresults.txt");
			if (argParser.hasFlag("-i"))
			{
				wordsMap.outputToFile("invertedindex.txt");
			}
		}
		else
		{
			System.out.println("Usage: java -cp project2.jar Driver -d <directory> -q <queryfile> [-i]");
		}
	}

}
