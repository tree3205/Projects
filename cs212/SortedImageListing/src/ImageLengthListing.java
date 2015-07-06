import java.io.File;

/*
 * Template file. Fill in the missing parts!
 */

public class ImageLengthListing extends SortedImageListing {

	public ImageLengthListing() {
		/*
		 * You have to call super() here with a Comparator object, defined as
		 * an anonymous inner class. The Comparator object should sort files
		 * such that the smallest file is listed first, and the largest file is
		 * listed last. You will want to use the File.length() method for this.
		 */


	}

	// Convenience constructor. You do not need to modify this.
	public ImageLengthListing(String path) {
		this(new File(path));
	}

	// Convenience constructor. You do not need to modify this.
	public ImageLengthListing(File start) {
		this();
		traverseDirectory(start);
	}

	/*
	 * Add the abstract method(s) you must implement here.
	 */




	/*
	 * Create the private static inner class that implements FileFilter here.
	 * You should only return true if the file is a directory, or is a file
	 * that is non-hidden and matches the provided regex. You will want to use
	 * file.isHidden(), file.getName().matches(regex), file.isDirectory(), and
	 * so on to create this class.
	 */




	/* Provided so you can easily run this file as a Java program. Will default
	 * to traversing the current directory if no command-line arguments are
	 * provided. To test on the lab computers, you should do:
	 *
	 *      java ImageLengthListing /home/public/sjengle/web
	 *
	 * The expected output is provided on the course website.
	 */
	public static void main(String[] args) {
		String directory = args.length > 0 ? args[0] : ".";
		ImageLengthListing listing = new ImageLengthListing(directory);
		listing.printFiles();
	}
}
