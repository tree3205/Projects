//:object/InvertedIndexBuilder.java

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Build invertedIndex with specific directory path. (Multi-thread version)
 * 
 * @author yxu66
 */
public class InvertedIndexBuilder 
{
	private InvertedIndex wordsMap;
	private WorkQueue workers;
	
	private int pending;
	
	InvertedIndexBuilder(InvertedIndex wordMap, int threads)
	{
		this.wordsMap = wordMap;
		this.workers = new WorkQueue(threads);
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
	 * Parse the given file and add words information into wordsMap.
	 * 	
	 * @param file the text file need to be parsed
	 */
	public void addFile(File file) {
		try {
			int position = 0;
			BufferedReader in;
			in = new BufferedReader(new FileReader(file));
			String line;
			InvertedIndex subMap = new InvertedIndex();
			while ((line = in.readLine()) != null)
			{
				ArrayList<String> words = StringParser.parseLineToStrArr(line);
				for (String word : words)
				{
					position++;
					subMap.addRecord(word, file.getPath(), position);
				}
			}
			in.close();
			wordsMap.addAll(subMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parse an text file in an individual thread.
	 */
	private class DirectoryWorker implements Runnable
	{
		private File dir;
		
		public DirectoryWorker(File dir)
		{
			this.dir = dir;
			updatePending(1);
		}

		@Override
		public void run() 
		{
			//System.out.println("Starting work on \"" + dir + "\"");

			File[] files = dir.listFiles();

			for(File f : files) {
				if(f.isDirectory() && f.canRead()) 
				{
					/*
					 * Create a new unit of work, so that another thread
					 * can handle this subdirectory.
					 */
					workers.execute(new DirectoryWorker(f));
				}
				else 
				{
					if (f.canRead() && f.getName().toLowerCase().endsWith(".txt"))
					{
						addFile(f);
					}
				}
			}
			/*
			 * Don't forget to indicate you finished a unit of work!
			 */
			updatePending(-1);
			//System.out.println("Finished work on \"" + dir + "\"");
		}
	}
	
	/**
	 * Build the invertedIndexMap with the files under a root directory
	 * 	
	 * @param dir the root directory needed to go through
	 */
	public void parseDir(File dir)
	{
		if(dir == null || !dir.canRead()) 
		{
			System.out.println("No directory to create listing for.");
			return;
		}
		
		if(dir.isFile()) 
		{
			addFile(dir);
			return;
		}
		
		workers.execute(new DirectoryWorker(dir));
		
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
