//:object/SearchResultFile.java

/**
 * Comparable search result object. So that we could use TreeSet to sort the search results.
 * 
 * @author yxu66
 */
public class SearchResultFile implements Comparable<SearchResultFile>
{
	
	private String filePath;
	private int frequency;
	private int position;
	
	public SearchResultFile(String filePath, int frequency, int position)
	{
		this.filePath = filePath;
		this.frequency = frequency;
		this.position = position;
	}
	
	/**
	 * Comparing method, frequency > position > alphabet order of URL
	 * 
	 * @param otherFile another search result object in the comparing
	 */
	public int compareTo(SearchResultFile otherFile) 
	{	
		if (this.frequency > otherFile.frequency)
		{
			return -1;
		}
		if (this.frequency < otherFile.frequency)
		{
			return 1;
		}
		
		if (this.position < otherFile.position)
		{
			return -1;
		}
		if (this.position > otherFile.position)
		{
			return 1;
		}
		
		if (this.filePath.compareTo(otherFile.filePath) < 0)
		{
			return -1;
		}
		if (this.filePath.compareTo(otherFile.filePath) > 0)
		{
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * Get the URL.
	 * @return filePath the URL.
	 */
	public String getFilePath()
	{
		return filePath;
	}
	
	/**
	 * Set the URL.
	 * @param filePath the URL.
	 */
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	/**
	 * Get frequency of the word.
	 * @return frequency the times of the appearance of the word.
	 */
	public int getFrequency()
	{
		return frequency;
	}
	
	/**
	 * Set frequency.
	 */
	public void setFrequency(int frequency)
	{
		this.frequency = frequency;
	}
	
	/**
	 * Get the word's position.
	 * @return positon the position that word appears.
	 */
	public int getPosition()
	{
		return position;
	}
	
	/**
	 * Set the word's position.
	 */
	public void setPosition(int position)
	{
		this.position = position;
	}
	
	/**
	 * The format of output.
	 */
	public String toString()
	{
		return '"' + getFilePath() + '"' + ", "  +  getFrequency() + ", " + getPosition();
	}

}
