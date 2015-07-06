//:object/MultiReaderLock.java

/**
 * Multi-reader and single-writer lock.
 * 
 * @author yxu66
 */
public class MultiReaderLock 
{
    private int writer;
    private int reader;
    
    
    public synchronized void acquireReadLock()
    {
    	while (writer > 0)
    	{
    		try
    		{
    			wait();
    		}
    		catch (InterruptedException ex)
    		{
    			
    		}
    	}
    	reader++;
    }
    
    public synchronized void releaseReadLock()
    {
    	reader--;
    	notifyAll();
    }
    
    public synchronized void acquireWriteLock() 
    {
    	while (reader > 0 || writer > 0)
    	{
    		try
    		{
    			wait();
    		}
    		catch (InterruptedException ex)
    		{
    			
    		}
    	}
    	writer++;
    }
    
    public synchronized void releaseWriterLock()
    {
    	writer--;
    	notifyAll();
    }
}