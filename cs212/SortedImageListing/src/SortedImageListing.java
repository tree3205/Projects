import java.io.File;
import java.util.Comparator;
import java.util.TreeSet;

/*
 * You should create a class ImageLengthListing that extends this class. You
 * will need to define a constructor that creates a custom Comparator as an
 * anonymous inner class, and a custom FileFilter class as a private static
 * inner class. Details are below.
 */

public abstract class SortedImageListing 
{

	/*
	 * This regex is provided for you. It will check if a file name ends in one
	 * of the following image extensions: png, gif, jpg, jpeg, tif, and tiff.
	 *
	 * For example, "logo.png".matches(regex) will return true, whereas
	 * "readme.txt".matches(regex) will return false.
	 */
	public static final String regex = ".*\\.(?i:(png)|(gif)|(jpe??g)|(tiff??))$";

	/*
	 * This is where the image file listing will be stored. Your class will
	 * not have access to this set directly. Instead, your class must use the
	 * addFile(File file) method to add files to this set.
	 */
	private TreeSet<File> files;

	/*
	 * This is the only constructor provided for this class. It requires a
	 * comparator that will be used to sort the files. You must call super()
	 * in your class with the comparator as an anonymous inner class. The
	 * comparator should sort files based on length such that the smallest file
	 * is listed first, and the largest file is listed last.
	 */
	public SortedImageListing(Comparator<File> comparator) 
	{
		files = new TreeSet<File>(comparator);
	}

	/*
	 * Prints file listing to console in the following format:
	 * <file size> <file absolute path>
	 *
	 * You may not override or modify this method.
	 */
	public final void printFiles() 
	{
		int width = String.valueOf(files.last().length()).length();
		String format = "%" + width + "d %s\n";

		for(File file : files) {
			System.out.printf(format, file.length(), file.getAbsolutePath());
		}
	}

	/*
	 * Adds the file to the set of files if (a) it is a file, and (b) it matches
	 * the regex of image file extensions. Use this method to add files to the
	 * sorted list of image files.
	 *
	 * You may not override or modify this method.
	 */
	public final void addFile(File file) 
	{
		if(file.isFile() && file.getName().matches(regex)) 
		{
			files.add(file);
		}
	}

	/*
	 * You must implement this method to recursively traverse a directory. You
	 * must use the listFiles(FileFilter filter) method from the File class
	 * with a custom FileFilter defined as a private static inner class.
	 *
	 * Your custom FileFilter should only accept if:
	 *
	 * (1) The file object is a directory, or...
	 *
	 * (2) The file object is a non-hidden file that matches the regex provided.
	 *
	 * You may want to use the isHidden(), isFile(), and isDirectory() methods
	 * from the File class.
	 */
	public abstract void traverseDirectory(File dir);

}
