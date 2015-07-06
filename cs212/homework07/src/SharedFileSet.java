import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.TreeSet;



public class SharedFileSet
{
   private MultiReaderLock lock;
   private TreeSet<File> set;
   
   public SharedFileSet()
   {
	   lock = new MultiReaderLock();
	   set = new TreeSet<File>();
   }
   
   public synchronized int size()
   {
	   int fileNums;
	   lock.acquireReadLock();
	   try
	   {
		   fileNums = set.size();
	   }
	   finally
	   {
		   lock.releaseReadLock();
	   }
	   return fileNums;
   }
   
   public synchronized void addFile(File file)
   {
	   lock.acquireWriteLock();
       try
       {
    	   set.add(file);
       }
	   finally
	   {
		   lock.releaseWriterLock();
	   }
   }
   
   public synchronized void addFiles(Collection<File> files)
   {
	   lock.acquireWriteLock();
	   try
	   {
		   set.addAll(files);
	   }
	   finally
	   {
		   lock.releaseWriterLock();
	   }
	   
   }
   
   public synchronized boolean hasFile(File file)
   {
	   lock.acquireReadLock();
	   try
	   {
		   if (file.exists())
		   {
			   return true;
		   }
		   else
		   {
			   return false;
		   }
	   }
	   finally
	   {
		   lock.releaseReadLock();
	   }
	   
   }

   public synchronized void printFiles()
   {
	   lock.acquireReadLock();
	   
	   PrintWriter writer = null;
	   String path = null;
	   try
	   {
		   writer = new PrintWriter(System.out, true);
		   
		   for(File file : set) 
		   {
				try 
				{
					path = file.getCanonicalPath();
				}
				catch(IOException ex) 
				{
					path = file.getAbsolutePath();
				}

				writer.println(path);
	        } 
	   }
	   finally
	   {
		   lock.releaseReadLock();
		   writer.flush();
	   }
   }
   
   public synchronized void printFiles(String filename)
   {
	   lock.acquireReadLock();
	   
	   PrintWriter writer = null;
	   String path = null;
	   try
	   {
		   writer = new PrintWriter(filename); 
		   
		   for(File file : set) 
		   {
				try 
				{
					path = file.getCanonicalPath();
				}
				catch(IOException ex) 
				{
					path = file.getAbsolutePath();
				}

				writer.println(path);
	        } 
	   }
	   catch (FileNotFoundException e) 
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		   lock.releaseReadLock();
		   writer.flush();
		   writer.close();
	   }
   }
}
    
    

