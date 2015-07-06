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
	 * Comparing method, frequency > position > alphabet order of file path
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
	
	public String getFilePath()
	{
		return filePath;
	}
	
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	
	public void setFrequency(int frequency)
	{
		this.frequency = frequency;
	}
	
	public int getPosition()
	{
		return position;
	}
	
	public void setPosition(int position)
	{
		this.position = position;
	}
	
	public String toString()
	{
		return '"' + getFilePath() + '"' + ", "  +  getFrequency() + ", " + getPosition();
	}

}
