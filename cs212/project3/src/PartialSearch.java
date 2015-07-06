//:object/PartialSearch.java

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;

/**
 * Do partial search with provided InvertedIndex map. (Multi-thread version)
 * 
 * @author yxu66
 */
public class PartialSearch 
{
	private InvertedIndex wordsMap;
	private WorkQueue workers;
	private ArrayList<String> queryOrder;
	private HashMap <String, TreeSet<SearchResultFile>> searchResultMap;
	private MultiReaderLock lock;

	private int pending;

	PartialSearch(InvertedIndex wordMap, int threads)
	{
		this.wordsMap = wordMap;
		this.workers = new WorkQueue(threads);
		this.queryOrder = new ArrayList<String>();
		this.searchResultMap = new HashMap <String, TreeSet<SearchResultFile>>();
		//String: single line in query file
		//TreeSet<SearchResultFile>: the search result of the key
		lock = new MultiReaderLock();
		this.pending = 0;
	}

	private synchronized int getPending() 
	{
		return pending;
	}

	private synchronized void updatePending(int amount) 
	{
		pending += amount;

		if(pending <= 0) 
		{
			notifyAll();
		}
	}

	/**
	 * Do partial search on the invertedIndexMap for the given query string in an 
	 * individual thread.
	 */
	private class SearchWorker implements Runnable
	{
		private String line;

		SearchWorker(String line)
		{
			this.line = line;
			updatePending(1);
		}

		@Override
		public void run() {
			//System.out.println("Starting search for QUERY: " + line);
			ArrayList<String> words = StringParser.parseLineToStrArr(line);
			TreeSet<SearchResultFile> searchResultSet = wordsMap.search(words);
			lock.acquireWriteLock();
			try
			{
				searchResultMap.put(line, searchResultSet);
			}
			finally
			{
				lock.releaseWriterLock();
			}
			updatePending(-1);
			//System.out.println("Finished search for QUERY: " + line);
		}
	}

	/**
	 * Read query words list from a specific file and do partial search based on
	 * the provided words map. Then output the results to specific file.
	 * 
	 * @param queryFile the file contains query words
	 * @param outputFilePath the path of the output file.
	 */
	public void partialSearch(File queryFile, String outputFilePath)
	{
		BufferedReader in;
		try 
		{
			in = new BufferedReader(new FileReader(queryFile));
			String line;
			while ((line = in.readLine()) != null)
			{
				synchronized(this)
				{
					queryOrder.add(line);
				}
				workers.execute(new SearchWorker(line));
			}
			in.close();
		} 
		catch (IOException e) 
		{
			System.out.println("Errors occured when reading query words.");
		}
		
		while(getPending() > 0) 
		{
			//System.out.println("Still working...");

			synchronized(this) 
			{
				try
				{
					wait();
				} 
				catch (InterruptedException ex) 
				{
					System.out.println("Interrupted while waiting.");
				}
			}
		}
		
		//System.out.println("Search complete.");
		//System.out.println("Starting output to File: " + outputFilePath);
		try {
			PrintWriter out = new PrintWriter(outputFilePath);
			
			for (String line : queryOrder)
			{
				out.println(line);
				for (SearchResultFile f : searchResultMap.get(line))
				{
					out.println(f.toString());
				}
				out.println();
			}
			out.close();
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Cannot create the output file.");
		}
		//System.out.println("Work complete.");
		shutdown();
	}
	
	/**
	 * Shutdown the workqueue.
	 */
	public void shutdown() 
	{
		workers.shutdown();
	}
}
