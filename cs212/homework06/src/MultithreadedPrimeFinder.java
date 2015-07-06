import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * You must fill in the missing components to create a multithreaded
 * prime finder using the PrimeHelper.primesByTrial() method. 
 */
public class MultithreadedPrimeFinder {

	/** Used for log4j debug messages. */
public static Logger log = Logger.getLogger(MultithreadedPrimeFinder.class);
  
/**
 * Uses multiple worker threads to find all primes less than or equal
 * to the maximum value. You must fill in this method!
 *
 * @param max maximum value to consider
 * @param threads number of worker threads to create
 * @return
 */
public static Collection<Integer> findPrimes(int max, int threads) throws InterruptedException {

	/*
	 * This is a public method, so we should do some validation of
	 * user input. The max value doesn't make sense if less than 2,
	 * but that won't actually cause any problems. However, we do
	 * need at least one worker thread.
	 */
	if(threads < 1) {
		throw new IllegalArgumentException("Number of worker threads" +
				" must be at least 1.");
	}

	
	/* ************ *
	 * FILL THIS IN *
	 * ************ */
    
	
	int remainder = max % threads;
	int chunkSize = max / threads;
	Collection<Integer> primes = new ArrayList<Integer>();
	
	PrimeWorker[] threadsWorker = new PrimeWorker[threads];
	



	for(int i = 0; i < threads - 1; i++) {
		threadsWorker[i] = new PrimeWorker(i * chunkSize, chunkSize);
		threadsWorker[i].start();
	}
	
		
	int last = threads - 1;
	threadsWorker[last] = new PrimeWorker(last * chunkSize, chunkSize + remainder);
	threadsWorker[last].start();
	
	for (PrimeWorker thread : threadsWorker) {
		thread.join();
		primes.addAll(thread.primes);
	}
		
		
	
		
	

	return primes;
}

/**
 * Private class that extends Thread to find a subset of primes. Make
 * sure you set the start and end values in the constructor, use the
 * PrimeHelper.primesByTrial() method, and remember to store the
 * primes you found somewhere.
 */
private static class PrimeWorker extends Thread {

	/* ************ *
	 * FILL THIS IN *
	 * ************ */

		private int start;
		private int chunkSize;
		public Collection<Integer> primes = new ArrayList<Integer>();

		
        public PrimeWorker(int start, int chunkSize) {
    	   this.start = start;
    	   this.chunkSize = chunkSize;
       }

        @Override
        public void run() {
        	primes = PrimeHelper.primesByTrial(start, start + chunkSize);
        }
	
		
	}	
 
	
}
