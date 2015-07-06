//:object/WorkQueue.java

import java.util.LinkedList;

/**
 * Work queue implementation adapted from
 * <a href="http://www.ibm.com/developerworks/library/j-jtp0730/index.html">
 * http://www.ibm.com/developerworks/library/j-jtp0730/index.htmll</a>.
 * 
 * Some modifications include removing raw types and adding shutdown capability comes from
 * <a href="http://cs212.cs.usfca.edu/lectures/multithreading/WorkQueue.java?attredirects=0&d=1">
 * http://cs212.cs.usfca.edu/lectures/multithreading/WorkQueue.java?attredirects=0&d=1</a>,
 * author: Sophie Eagle.
 *
 * My modification include remove the nThreads attribute and use standard output to replace the log4j
 * messages.
 * 
 * @author yxu66
 */
public class WorkQueue
{

    private final PoolWorker[] threads;
    private final LinkedList<Runnable> queue;
    private volatile boolean shutdown;

    public WorkQueue(int nThreads)
    {
        queue = new LinkedList<Runnable>();
        threads = new PoolWorker[nThreads];
        
        shutdown = false;

        for (int i=0; i<nThreads; i++) 
        {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }

    public void execute(Runnable r) 
    {
        synchronized(queue) 
        {
            queue.addLast(r);
            queue.notify();
        }
    }
    
    public void shutdown() 
    {
        shutdown = true;

        synchronized(queue) 
        {
        	queue.notifyAll();
        }
    }

    private class PoolWorker extends Thread 
    {
        public void run() 
        {
            Runnable r = null;

            while (true) 
            {
                synchronized(queue) 
                {
                    while (queue.isEmpty() && !shutdown)
                    {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        	System.out.println("Interrupted while waiting for work.");
                        }
                    }
                    
                    if(shutdown) 
                    {
                        //System.out.println("Shutdown detected.");
                        break;
                    }
                    else 
                    {
                        assert !queue.isEmpty() : "Work queue empty.";
                        r = queue.removeFirst();
                    }
                }

                // If we don't catch RuntimeException, 
                // the pool could leak threads
                try 
                {
                	//System.out.println("Found work!");
                	assert r != null : "Runnable object null.";
                    r.run();
                }
                catch (RuntimeException e) 
                {
                	System.out.println("Encountered exception running work.");
                	e.printStackTrace();
                }
            }
            
            //System.out.println("Thread terminating.");
        }
    }
}