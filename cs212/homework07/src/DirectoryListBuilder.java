
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

public class DirectoryListBuilder {
	private static Logger log = Logger.getLogger(DirectoryListBuilder.class);

	/*
	 * We will keep the specific type used for dirlist unspecified. This
	 * will let us switch between different types of lists and sets
	 * depending on the situation.
	 */

	private SharedFileSet dirlist;
	private WorkQueue workers;

	/*
	 * Notice now that we can no longer call join() to wait for a thread
	 * to finish. And, our threads never "finish" since they are always
	 * waiting for more work. So, we'll need to track when all of the
	 * work is done ourselves.
	 */
	private int pending;

	DirectoryListBuilder(int threads) {
		dirlist = new SharedFileSet();
		workers = new WorkQueue(threads);
		pending = 0;

		log.debug("Maximum of " + threads + " worker threads.");
	}

	DirectoryListBuilder() {
		this(5);
	}

	/*
	 * We know we have two shared objects, dirlist and pending. We can
	 * go ahead and create some synchronized helper methods for accessing
	 * those shared objects.
	 */

	public synchronized int numResults() {
		return dirlist.size();
	}

	private void addResult(File file) {
		dirlist.addFile(file);
	}

	private void addResults(Collection<File> sublist) {
		dirlist.addFiles(sublist);
	}

	private int getPending() {
		return pending;
	}

	private synchronized void updatePending(int amount) {
		pending += amount;

		/*
		 * Try this without the notifyAll() below first. What happens to the
		 * runtime, and how does it relate to the wait time?
		 */

		/*
		 * How often will we need to call notifyAll()? Should we every
		 * time pending changes? Or...
		 */

		if(pending <= 0) {
			notifyAll();
		}

		/*
		 * We could test if pending == 0, but testing if it is <= 0 will still
		 * notify our threads in case there is a bug.
		 */
	}


	/*
	 * What happens if we call a synchronized method instead another
	 * synchronized method? Will we be able to obtain the lock's key?
	 */

	public void printResults() {
		dirlist.printFiles();
	}

	public void printResults(String filename) {
		dirlist.printFiles(filename);
	}

	/*
	 * When you deadlock in your code, always see if you are accessing
	 * synchronized blocks in a nested way. If so, double-check if those blocks
	 * are using the same lock!
	 */

	/*
	 * Next, lets decide what our worker thread should do.
	 */
	private class DirectoryWorker implements Runnable {
		private File dir;

		public DirectoryWorker(File dir) {
			this.dir = dir;

			/*
			 * Make sure you indicate there is an outstanding work
			 * request.
			 */
			updatePending(1);

			/*
			 * Can we use a synchronized block using "this" as the lock
			 * to update the pending variable?
			 *
			 * synchronized(this) {
			 * 		pending += 1;
			 * }
			 */
		}

		@Override
		public void run() {
			log.debug("Starting work on \"" + dir + "\"");

			/*
			 * We will use an ArrayList here since we just need to be
			 * able to append quickly.
			 */
			ArrayList<File> subresults = new ArrayList<File>();
			File[] files = dir.listFiles();

			for(File f : files) {
				if(f.isDirectory() && f.canRead()) {
					/*
					 * Create a new unit of work, so that another thread
					 * can handle this subdirectory.
					 */
					workers.execute(new DirectoryWorker(f));
				}
				else {
					subresults.add(f);
				}
			}

			/*
			 * We will keep a list of sub-results instead of adding every
			 * file to the master directory list each time. We do this so
			 * that we reduce how often we block other threads (which is
			 * very costly time-wise).
			 */
			addResults(subresults);

			/*
			 * Don't forget to indicate you finished a unit of work!
			 */
			updatePending(-1);
			log.debug("Finished work on \"" + dir + "\"");
		}
	}

	/*
	 * Finally, we can create the primary method for this class that
	 * creates a listing for a particular directory.
	 */

	public void parseDir(File dir) {
		/*
		 * This is a public method, so we need to do validation of user
		 * input.
		 */

		if(dir == null || !dir.canRead()) {
			log.warn("No directory to create listing for.");
			return;
		}

		if(dir.isFile()) {
			addResult(dir);
			return;
		}

		/*
		 * At this point, we know we are dealing with a non-null readable
		 * directory.
		 */

		workers.execute(new DirectoryWorker(dir));

		/*
		 * We need to wait until all of the work is finished. We can't
		 * join the worker threads anymore, so we will use the pending
		 * variable instead.
		 */
		while(getPending() > 0) {
			log.info("Still working...");

			synchronized(this) {
				try {
					// This just gives us periodic updates so we don't
					// think the program froze.

					/*
					 * If you don't have a notifyAll() call, how does this
					 * affect the runtimes?
					 */
					wait(1500);
				} catch (InterruptedException ex) {
					log.warn("Interrupted while waiting.", ex);
				}
			}
		}

		log.debug("Work complete.");
		// workers.shutdown();

		// We may not want to shutdown here, in case other directories will be
		// parsed later.
	}

	/*
	 * This lets the main class control when to shutdown the work queue.
	 */
	public void shutdown() {
		workers.shutdown();
	}
}
