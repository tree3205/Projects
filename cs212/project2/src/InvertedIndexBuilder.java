//:object/InvertedIndexBuilder.java

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.BufferedReader;

/**
 * Build invertedIndex with specific directory path.
 * 
 * @author yxu66
 */
public class InvertedIndexBuilder 
{
	
	/**
	 * The method to traverse the directory including subdirectory to find all the text files
	 * and build the invertedIndex. Read and parse every line of the text files into word, 
	 * then add their positions, the path of the file and the word into a nested TreeMap.
	 * 
	 * @param wordMap the instance of InvertedIndex, which is a nested TreeMap
	 * @param root the directory of file
	 */
	public static void buildMap(InvertedIndex wordMap, File root) 
	{
		try
		{
			String[] fileNameList = root.list();

			for (String fileName : fileNameList)
			{
				File file = new File(root.getPath(), fileName);

				if (file.isFile() && file.getName().toLowerCase().endsWith(".txt"))
				{
					int position = 0;
					BufferedReader in = new BufferedReader(new FileReader(file));
					String line;
					while ((line = in.readLine()) != null)
					{
						ArrayList<String> words = StringParser.parseLineToStrArr(line);
						for (String word : words)
						{
							position++;
							wordMap.addRecord(word, file.getPath(), position);
						}
					}
					in.close();
				}
				else
				{
					buildMap(wordMap, file);
				} 
			}
		}
		catch (Exception exception)
		{
			System.out.println("The file cannot be read. Ignored...");
		}
	}

}
