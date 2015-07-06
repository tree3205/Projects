import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Assumptions:
 * Assume the HTML is valid (i.e. passes http://validator.w3.org/) and
 * all href attributes are quoted and URL encoded.
 */

public class HTMLLinkParser {

	// You must fill in this regular expression.

	
	public static final String regex = "\"([^\">]+\\.[^\">]+)\"";


	/*
	 * If you only have one capturing group for the link, you can use
	 * this function directly. Otherwise, you will need to change which
	 * group is added to the list of links.
	 */
	public static ArrayList<String> listLinks(String text) {
		ArrayList<String> links = new ArrayList<String>();

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);

		int start = 0;

		while(m.find(start)) {
			// Change this if necessary.
			
			links.add(m.group(1));
			start = m.end();
		}

		return links;
	}
}
