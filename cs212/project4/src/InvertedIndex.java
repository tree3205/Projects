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
 * Thread-safe nested TreeMap named invertedIndexMap,which the key is every word in the text files,
 * and the value is another TreeMap which is the specific position of the word.
 * 
 * @author yxu66
 */
public class InvertedIndex 
{

	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> invertedIndexMap;
	private MultiReaderLock lock;

	public InvertedIndex()
	{
		invertedIndexMap = new TreeMap<String, TreeMap<String, ArrayList<Integer>>>();
		lock = new MultiReaderLock();
	}
	
	/**
	 * Add a word with URL and position into invertedIndexMap.
	 * 	
	 * @param word words in the html.
	 * @param file the URL under the seed URL.
	 * @param position the position of the word.
	 * @param tmpWordMap a TreeMap to store the word and its position temporarily.
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
	 * Add the invertedIndexMap of sub InvertedIndex instance into invertedIndexMap.
	 * 	
	 * @param subInvertedIndex the given sub InvertedIndex instance
	 */
	public void addAll(InvertedIndex subInvertedIndex)
	{
		lock.acquireWriteLock();
		try
		{
			for(String word : subInvertedIndex.invertedIndexMap.keySet())
			{
				if(!invertedIndexMap.containsKey(word)) 
				{
					invertedIndexMap.put(word, subInvertedIndex.invertedIndexMap.get(word));
				}
				else
				{
					Map.Entry<String, ArrayList<Integer>> entry = subInvertedIndex.invertedIndexMap.get(word).firstEntry();
					invertedIndexMap.get(word).put(entry.getKey(), entry.getValue());
				}
			}
		}
		finally
		{
			lock.releaseWriterLock();
		}
	}

	/**
	 * Output the invertedIndexMap to specific file.
	 * 
	 * @param outputFilePath the path of the output file.
	 * @param subMap a TreeMap store the position of words.
	 */
	public void outputToFile(String outputFilePath)
	{
		lock.acquireReadLock();
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
		finally
		{
			lock.releaseReadLock();
		}
	}

	/**
	 * Search specific query word in invertedIndexMap. Return a TreeSet of SearchResultFile instances,
	 * which contain the URl where the word occurs, the word frequencies of the URLs and
	 * the first position of the word.
	 * 
	 * @param words the query words list.
	 * @param searchResultHash a HashMap that the key is URL, and the value is another TreeSet of SearchResultFile instances.
	 * @return searchResultSet a TreeSet of SearchResultFile instances.
	 */
	public TreeSet<SearchResultFile> search(ArrayList<String> words)
	{
		TreeSet<SearchResultFile> searchResultSet = new TreeSet<SearchResultFile>();
		HashMap<String, SearchResultFile> searchResultHash = new HashMap<String, SearchResultFile>();
		SearchResultFile tmpResultFile;
		TreeMap<String, ArrayList<Integer>> tmpFileMap;
		String tmpFilePath;
		ArrayList<Integer> tmpPositionList;

		lock.acquireReadLock();
		try
		{
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
		}
		finally
		{
			lock.releaseReadLock();
		}

		searchResultSet.addAll(searchResultHash.values());
		
		return searchResultSet;
	}
}
