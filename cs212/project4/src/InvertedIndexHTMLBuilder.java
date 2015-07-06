//:object/InvertedIndexHTMLBuilder.java

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build invertedIndex with specific url.
 * 
 * @author yxu66
 */
public class InvertedIndexHTMLBuilder 
{
	private InvertedIndex wordsMap;
	private WorkQueue workers;
	private ArrayList<URL> closedList;
	private MultiReaderLock builderlock;
	private int maxPageNum;
	private int pageNum;

	private int pending;

	InvertedIndexHTMLBuilder(InvertedIndex wordMap, int threads, int maxPageNum)
	{
		this.wordsMap = wordMap;
		this.workers = new WorkQueue(threads);
		// crawled url list
		closedList = new ArrayList<URL>();
		// lock to make closedlist thread-safe
		builderlock = new MultiReaderLock();
		this.maxPageNum = maxPageNum;
		this.pageNum = 0;
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

	private synchronized void updatePageNum(int amount)
	{
		pageNum += amount;
	}

	private synchronized boolean isMaxPageNum()
	{
		if (pageNum >= maxPageNum)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Parse a web page in an individual thread.
	 */
	private class HTMLWorker implements Runnable
	{
		public static final int PORT = 80;

		private String domain;
		private String resource;
		private URL url;

		private InvertedIndex subInvertedIndex;

		/**
		 * Creates a new HTMLWorker object for the specified URL. If the
		 * URL is not valid, uses 120.0.0.1 for the domain and / for the
		 * resource by default.
		 *
		 * @param url location of web page
		 */
		public HTMLWorker(URL url) 
		{

			this.url = url;
			builderlock.acquireWriteLock();
			try
			{
				//System.out.println("Start working on: " + url.toString());
				closedList.add(url);
			}
			finally
			{
				builderlock.releaseWriterLock();
				//System.out.println("WriterLock released.");
			}
			subInvertedIndex = new InvertedIndex();
			updatePending(1);
			try 
			{
				this.domain = url.getHost();
				this.resource = url.getFile();
			}
			catch(Exception ex) 
			{
				this.domain = "127.0.0.1";
				this.resource = "/";
			}
		}

		/**
		 * Tests whether a start tag exists for the element in the buffer. For
		 * example, <code>startElement("style", buffer)</code> will test if
		 * the buffer contains the start <style> tag.
		 *
		 * @param element the element name, like "style" or "script"
		 * @param buffer the html code being checked
		 * @return true if the start element tag exists in the buffer
		 */
		private boolean startElement(String element, String buffer) {
			Pattern p = Pattern.compile(element);
			Matcher m = p.matcher(buffer);
			if(m.find())
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		/**
		 * Tests whether a close tag exists for the element in the buffer. For
		 * example, <code>closeElement("style", buffer)</code> will test if
		 * the buffer contains the close </style> tag.
		 * @param element the element name, like "style" or "script"
		 * @param buffer the html code being checked
		 * @return true if the start element tag exists in the buffer
		 */
		private boolean closeElement(String element, String buffer) 
		{
			Pattern p = Pattern.compile(element);
			Matcher m = p.matcher(buffer);
			if(m.find())
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		/**
		 * Removes the element tags and all text between from the buffer. For
		 * example, <code>stripElement("style", buffer)</code> will return a
		 * string with the <style>...</style> tags and all text inbetween
		 * removed. Both the start and close tags must be present.
		 *
		 * @param element the element name, like "style" or "script"
		 * @param buffer the html code being checked
		 * @return text without the start and close tags and all text inbetween
		 */
		private String stripElement(String element, String buffer) 
		{

			return buffer.replaceAll(element, "");


		}

		/**
		 * Replaces all HTML entities in the buffer with a single space. For
		 * example, "2010&ndash;2012" will become "2010 2012".
		 *
		 * @param buffer the html code being checked
		 * @return text with HTML entities replaced by a space
		 */
		private String stripEntities(String buffer) 
		{
			String regex = "&[^;]+;";
			String text = buffer.replaceAll(regex, " ");

			return text;
		}

		/**
		 * Replaces all HTML tags in the buffer with a single space. For
		 * example, "hello <b>world</b>!" will become "hello  world !".
		 *
		 * @param buffer the html code being checked
		 * @return text with HTML tags replaced by a space
		 */
		private String stripHTML(String buffer) 
		{
			String regex = "<[^<]*>";
			String text = buffer.replaceAll(regex, " ");

			return text;
		}

		/**
		 * Opens a socket and downloads the webpage one line at a time,
		 * removing all text between style or script tags, removing HTML tags,
		 * and removing special symbols (HTML entities). Stores only one line
		 * of the webpage at once unless additional lines are necessary to
		 * parse missing close tags. Each cleaned line of text is then parsed
		 * into words and placed into a sub invertedIndex. Then add the sub 
		 * invertedIndex into wordsMap.
		 */

		@Override
		public void run() 
		{	
			BufferedReader reader = null;
			PrintWriter writer = null;
			Socket socket = null;

			StringBuffer buffer = new StringBuffer();
			String currentLine;

			boolean valid = true;
			boolean open = false;
			boolean tagOpen = false;

			String elementTagStart = "<script[^>]*>|<style[^>]*>";
			String elementTagEnd = "</script>|</style>";
			String elementTag = "<script[^>]*>.*</script>|<style[^>]*>.*</style>";

			while (true)
			{
				try {
					socket = new Socket(domain, PORT);
					writer = new PrintWriter(socket.getOutputStream());
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					String request = "GET " + resource + " HTTP/1.0\n";

					writer.println(request);
					writer.flush();

					String line;
					line = reader.readLine();


					while (line != null) {
						if (line.startsWith("Content-Type:") && !line.startsWith("Content-Type: text/html"))
						{
							valid = false;
						}
						if (line.length() == 0)
							break;
						line = reader.readLine();
					}

					if (valid)
					{
						//System.out.println("currentNum: " + pageNum);
						int position = 0;
						line = reader.readLine();
						if (line.contains("<") && !line.contains(">"))
						{
							tagOpen = true;
						}
						if (startElement(elementTagStart, line) && !closeElement(elementTagEnd, line))
						{
							open = true;
						}

						while (line != null) 
						{
							//System.out.println(line);
							currentLine = line;
							line = reader.readLine();
							buffer.append(currentLine);

							if (tagOpen) 
							{
								if (currentLine.contains(">"))
								{
									tagOpen = false;
								}
								else
								{
									continue;
								}
							}

							if (open) 
							{
								if (closeElement(elementTagEnd, currentLine)) 
								{
									open = false;
								}
								else
								{
									continue;
								}
							}

							if (currentLine.contains("<") && !currentLine.contains(">"))
							{
								tagOpen = true;
								continue;
							}
							if (startElement(elementTagStart, currentLine) && !closeElement(elementTagEnd, currentLine))
							{
								open = true;
								continue;
							}

							String rawHTML = buffer.toString();
							String tmpText = stripElement(elementTag, rawHTML);
							tmpText = stripEntities(tmpText);
							tmpText = stripHTML(tmpText);
							for (String word : StringParser.parseLineToStrArr(tmpText))
							{
								position++;
								subInvertedIndex.addRecord(word, url.toString(), position);
							}
							ArrayList<String> links = new ArrayList<String>();
							links = HTMLLinkParser.listLinks(rawHTML);
							for (String l : links)
							{
								URL absolute = new URL(url, l);
								boolean isNewURL = true;
								builderlock.acquireReadLock();
								try
								{
									for (URL closedURL : closedList)
									{
										if (closedURL.sameFile(absolute))
										{
											isNewURL = false;
										}
									}
								}
								finally
								{
									builderlock.releaseReadLock();
								}
								if (isNewURL && !isMaxPageNum())
								{
									//System.out.println("Find: " + absolute.toString());
									updatePageNum(1);
									workers.execute(new HTMLWorker(absolute));
								}
							}
							buffer = new StringBuffer();
						}
					}
				}
				catch (MalformedURLException e) 
				{
					System.out.println("Got bad url. Ignore...");
				}
				catch (UnknownHostException e) 
				{
					System.out.println("Unknown Host Found. Retasking...");
				} 
				catch (IOException e) 
				{
					System.out.println("IOException found when reading remote data. Retrying...");
				}
				finally
				{
					try 
					{
						reader.close();
						writer.close();
						socket.close();
					}
					catch(Exception ignored)
					{
						// do nothing
					}
				}
				break;
			}
			wordsMap.addAll(subInvertedIndex);
			updatePending(-1);
		}
	}

	/**
	 * Build the invertedIndexMap with the files under a seed URL.
	 * 	
	 * @param dir the seed URL needed to go through
	 */
	public void parseURL(URL url)
	{
		updatePageNum(1);
		workers.execute(new HTMLWorker(url));

		while(getPending() > 0) 
		{
			//System.out.println("Still working...");
			//System.out.println(getPending());

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
