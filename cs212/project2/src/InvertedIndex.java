//:object/InvertedIndex.java

import java.io.FileNotFoundException;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Nested TreeMap named invertedIndexMap,which the key is every word in the text files,
 * and the value is another TreeMap which is the specific position of the word.
 * 
 * @author yxu66
 */
public class InvertedIndex 
{

	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> invertedIndexMap;

	public InvertedIndex()
	{
		invertedIndexMap = new TreeMap<String, TreeMap<String, ArrayList<Integer>>>();
	}
	
	/**
	 * Add a word with file path and position into invertedIndexMap.
	 * 	
	 * @param word words in the text file
	 * @param file the text file under the directory
	 * @param position the position of the word
	 */
	public void addRecord(String word, String file, Integer position)
	{
		TreeMap<String, ArrayList<Integer>> tmpWordMap = new TreeMap<String, ArrayList<Integer>>();
		ArrayList<Integer> tmpWordPositionList = new ArrayList<Integer>();
		if ((tmpWordMap = invertedIndexMap.get(word)) != null)
		{
			if ((tmpWordPositionList = tmpWordMap.get(file)) != null)
			{
				if (!tmpWordPositionList.contains(position))
				{
					tmpWordPositionList.add(position);
				}
			}
			else
			{
				tmpWordPositionList = new ArrayList<Integer>();
				tmpWordPositionList.add(position);
				tmpWordMap.put(file, tmpWordPositionList);
			}
		}
		else
		{
			tmpWordPositionList = new ArrayList<Integer>();
			tmpWordPositionList.add(position);
			tmpWordMap = new TreeMap<String, ArrayList<Integer>>();
			tmpWordMap.put(file, tmpWordPositionList);
			invertedIndexMap.put(word, tmpWordMap);
		}
	}
	
	/**
	 * Output the invertedIndexMap to specific file.
	 * 
	 * @param outputFilePath the path of the output file.
	 */
	public void outputToFile(String outputFilePath)
	{
		try
		{
			PrintWriter out = new PrintWriter(outputFilePath);
			Iterator<String> wordSet = invertedIndexMap.keySet().iterator();
			while (wordSet.hasNext())
			{
				String word = wordSet.next().toString();
				out.println(word);
				TreeMap<String, ArrayList<Integer>> subMap = invertedIndexMap.get(word);
				Iterator<String> fileSet = subMap.keySet().iterator();
				while (fileSet.hasNext())
				{
					String filePath = fileSet.next().toString();
					String posArrStr = subMap.get(filePath).toString();
					String posStr = posArrStr.substring(1, posArrStr.length()-1);
					out.println('"' + filePath + '"' + ", " + posStr);
				}
				out.println();
			}
			out.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File cannot be found.");
		}
	}

	/**
	 * Search specific query word in invertedIndexMap. Return a TreeSet of SearchResultFile instances,
	 * which contain the path of files where the word occurs, the word frequencies of the files and
	 * the first position of the word.
	 * 
	 * @param words the query words list
	 */
	public TreeSet<SearchResultFile> search(ArrayList<String> words)
	{
		TreeSet<SearchResultFile> searchResultSet = new TreeSet<SearchResultFile>();
		HashMap<String, SearchResultFile> searchResultHash = new HashMap<String, SearchResultFile>();
		SearchResultFile tmpResultFile;
		TreeMap<String, ArrayList<Integer>> tmpFileMap;
		String tmpFilePath;
		ArrayList<Integer> tmpPositionList;

		for (String word : words)
		{
			for (String key = invertedIndexMap.ceilingKey(word); key != null; key = invertedIndexMap.higherKey(key))
			{
				if (!key.startsWith(word))
				break;
				tmpFileMap = invertedIndexMap.get(key);
				for (Map.Entry<String, ArrayList<Integer>> entry : tmpFileMap.entrySet())
				{
					tmpFilePath = entry.getKey();
					tmpPositionList = entry.getValue();
					if (searchResultHash.containsKey(tmpFilePath))
					{
						tmpResultFile = searchResultHash.get(tmpFilePath);
						tmpResultFile.setFrequency(tmpResultFile.getFrequency() + tmpPositionList.size());
						if (tmpResultFile.getPosition() > tmpPositionList.get(0))
						{
							tmpResultFile.setPosition(tmpPositionList.get(0));
						}
					}
					else
					{
						tmpResultFile = new SearchResultFile(tmpFilePath, tmpPositionList.size(), tmpPositionList.get(0));
						searchResultHash.put(tmpFilePath, tmpResultFile);
					}
				}
			}
		}
		
		for (Map.Entry<String, SearchResultFile> entry: searchResultHash.entrySet())
		{
			tmpResultFile = entry.getValue();
			searchResultSet.add(tmpResultFile);
		}
		return searchResultSet;
	}
}
