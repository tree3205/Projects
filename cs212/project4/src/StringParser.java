//:object/StringParser.java

import java.util.ArrayList;

/**
 * Deal with the String issue.
 * 
 * @author yxu66
 * */
public class StringParser 
{
	/**
	 * Read a line of the text files, then parse and store the words in an ArrayList.
	 * 
	 * @param line a line of words from the text files
	 * @return parsedStrArr an ArrayList store the parsed words.
	 */
		
	public static ArrayList<String> parseLineToStrArr(String line)
	{
		ArrayList<String> parsedStrArr = new ArrayList<String>();
		
		String[] words = line.split("\\s+");
		
		for (String word : words)
		{
			String wordCooked = word.replaceAll("[^A-Za-z0-9]+", "").toLowerCase();
			if (wordCooked.matches("\\s*"))
			{
				continue;
			}
			parsedStrArr.add(wordCooked);
		}
		
		return parsedStrArr;
	}
}
