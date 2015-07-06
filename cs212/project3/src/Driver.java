//:object/Driver.java
import java.io.File;

/**
 * Test the project3
 * 
 * @author yxu66
 */
public class Driver 
{

	/**
	 * Build an invertedIndexMap based on given directory, do partial search on the
	 * invertedIndexMap and output the result to a certain file.
	 * Output the specific invertedIndexMap of provided directory to a certain file if required.
	 * 
	 * @param args the argument that input to test the program.
	 */
	public static void main(String[] args) 
	{	
		ArgumentParser argParser = new ArgumentParser(args);
		if (argParser.hasFlag("-d") && argParser.hasFlag("-q") && argParser.hasFlag("-t"))
		{
			String rootPath = argParser.getValue("-d");
			File root = new File(rootPath);
			String queryFilePath = argParser.getValue("-q");
			File queryFile = new File(queryFilePath);
			try
			{
				int threads = Integer.parseInt(argParser.getValue("-t"));
				InvertedIndex wordsMap = new InvertedIndex();
				InvertedIndexBuilder mapBuilder = new InvertedIndexBuilder(wordsMap, threads);
				mapBuilder.parseDir(root);
				PartialSearch partialSearcher = new PartialSearch(wordsMap, threads);
				partialSearcher.partialSearch(queryFile, "searchresults.txt");
				if (argParser.hasFlag("-i"))
				{
					wordsMap.outputToFile("invertedindex.txt");
				}
			}
			catch (Exception e)
			{
				System.out.println("<threads> should be an Integer.");
			}
		}
		else
		{
			System.out.println("Usage: java -cp project2.jar Driver -d <directory> -q <queryfile> -t <threads> [-i]");
		}
	}

}
