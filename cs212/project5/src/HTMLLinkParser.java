//:object/HTMLLinkParser.java

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse the given text and find all the url.
 * 
 * @author yxu66
 */

public class HTMLLinkParser 
{

	public static final String aTagRegex = "<[Aa][^>]+>";
	public static final String linkRegex = "(?<=\")([^\">]+\\.[^\">]+)(?=\")";

	/**
	 * parse the given text and find all urls in it, which are returned in a Arraylist
	 *
	 * @param text given text used to parse and find url in it
	 */
	public static ArrayList<String> listLinks(String text) 
	{
		ArrayList<String> links = new ArrayList<String>();

		Pattern aTagPattern = Pattern.compile(aTagRegex);
		Pattern linkPattern = Pattern.compile(linkRegex);
		Matcher aTagMatcher = aTagPattern.matcher(text);

		while(aTagMatcher.find())
		{
			Matcher linkMatcher = linkPattern.matcher(aTagMatcher.group());
			while (linkMatcher.find())
			{
				String url = linkMatcher.group();
				if (!url.contains("mailto"))
				{
					links.add(linkMatcher.group());
				}
			}
		}

		return links;
	}
}