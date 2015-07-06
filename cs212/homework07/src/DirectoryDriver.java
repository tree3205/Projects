
import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class DirectoryDriver {

	private static Logger log = Logger.getLogger(DirectoryDriver.class);

	public static void main(String[] args) throws IOException {

		if(new File("log4j.properties").canRead() == false){
			BasicConfigurator.configure();
		} else {
			PropertyConfigurator.configure("log4j.properties");
		}

		/*
		 * If you want to disable logging for the WorkQueue class,
		 * add "log4j.category.WorkQueue=OFF" to the end of your
		 * log4j.properties file.
		 */

		File dir = null;
		int threads = 0;

		try {
			dir = new File(args[0]);
		}
		catch(Exception ex) {
			dir = new File(".");
		}

		try {
			threads = Integer.parseInt(args[1]);
		}
		catch(Exception ex) {
			threads = 5;
		}

		log.info("Creating sorted directory listing for \"" + dir.getCanonicalPath() + "\".");

		long startTime = System.currentTimeMillis();
		DirectoryListBuilder dlb = new DirectoryListBuilder(threads);
		dlb.parseDir(dir);
		double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;

		if(dlb.numResults() < 20) {
			dlb.printResults();
		}

		String filename = dir.getCanonicalFile().getName() + ".listing.txt";
		dlb.printResults(filename);
		log.info("Results written to file " + filename + ".");

		log.info(String.format("Finished. Took %.3f seconds.\n", elapsed));

		dlb.shutdown();	// Preferred over System.exit().

//		System.exit(0);
		/*
		 * Calling System.exit() explicitly will hide any issues you have with
		 * runaway threads. If your code works properly, it is unnecessary!
		 */
	}
}
