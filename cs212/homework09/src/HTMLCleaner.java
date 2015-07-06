import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class HTMLCleaner {

	public static final int PORT = 80;

	private String domain;
	private String resource;

	/**
	 * Creates a new HTMLCleaner object for the specified URL. If the
	 * URL is not valid, uses 120.0.0.1 for the domain and / for the
	 * resource by default.
	 *
	 * @param url location of webpage
	 */
	public HTMLCleaner(String url) {
		try {
			URL link = new URL(url);
			this.domain = link.getHost();
			this.resource = link.getFile();
		}
		catch(Exception ex) {
			this.domain = "127.0.0.1";
			this.resource = "/";
		}
	}

	/**
	 * Parses a line of text into lowercase words with no symbols, and
	 * adds those words to a list.
	 *
	 * @param buffer text containing words separated by whitespaces
	 */
	private void parseLine(String buffer, ArrayList<String> words) {
		for(String word : buffer.split("\\s+")) {
			word = word.toLowerCase().replaceAll("[\\W_]", "").trim();

			if(!word.isEmpty()) {
				words.add(word);
			}
		}
	}

	/**
	 * Tests whether a start tag exists for the element in the buffer. For
	 * example, <code>startElement("style", buffer)</code> will test if
	 * the buffer contains the start <style> tag.
	 *
	 * @param element the element name, like "style" or "script"
	 * @param buffer the html code being checked
	 * @return true if the start element tag exists in the buffer
	 */
	public static boolean startElement(String element, String buffer) {
		
		
	
        if(buffer.startsWith(element))
		{
			return true;
		}
        else
        {
		
		    return false;
        }
		
	}

	/**
	 * Tests whether a close tag exists for the element in the buffer. For
	 * example, <code>closeElement("style", buffer)</code> will test if
	 * the buffer contains the close </style> tag.
	 * @param element the element name, like "style" or "script"
	 * @param buffer the html code being checked
	 * @return true if the start element tag exists in the buffer
	 */
	public static boolean closeElement(String element, String buffer) {
		
		if(buffer.endsWith(element))
		{
			return true;
		}
        else
        {
		
		    return false;
        }

	}

	/**
	 * Removes the element tags and all text between from the buffer. For
	 * example, <code>stripElement("style", buffer)</code> will return a
	 * string with the <style>...</style> tags and all text inbetween
	 * removed. Both the start and close tags must be present.
	 *
	 * @param element the element name, like "style" or "script"
	 * @param buffer the html code being checked
	 * @return text without the start and close tags and all text inbetween
	 */
	public static String stripElement(String element, String buffer) {
		
		// fill this in
	    return buffer.replaceAll(element, "");
	    

	}

	/**
	 * Replaces all HTML entities in the buffer with a single space. For
	 * example, "2010&ndash;2012" will become "2010 2012".
	 *
	 * @param buffer the html code being checked
	 * @return text with HTML entities replaced by a space
	 */
	public static String stripEntities(String buffer) {
		//fill this in
		String regex = "&\\w+;";
		String text = buffer.replaceAll(regex, " ");
		
		return text;
	  
	
	}

	/**
	 * Replaces all HTML tags in the buffer with a single space. For
	 * example, "hello <b>world</b>!" will become "hello  world !".
	 *
	 * @param buffer the html code being checked
	 * @return text with HTML tags replaced by a space
	 */
	public static String stripHTML(String buffer) {
		//fill this in
		String regex = "<[^<]+?>";
		String text = buffer.replaceAll(regex, " ");
		
		return text;
	}


	/**
	 * Opens a socket and downloads the webpage one line at a time,
	 * removing all text between style or script tags, removing HTML tags,
	 * and removing special symbols (HTML entities). Stores only one line
	 * of the webpage at once unless additional lines are necessary to
	 * parse missing close tags. Each cleaned line of text is then parsed
	 * into words and placed into a list.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public ArrayList<String> parseWords()  {

		// fill this in
		ArrayList<String> words = new ArrayList<String>();
		
 		BufferedReader reader = null;
 		PrintWriter writer = null;
 		Socket socket = null;
 		
 		boolean open = false;
 		StringBuffer buffer = new StringBuffer();
 		
        String elementTagStart = "<script[^>]*>|<style[^>]*>|<code[^>]*>";
        String elementTagEnd = "</script>|</style>|</code>";
        String elementTag = "<script[^>]*>.*</script>|<style[^>]*>.*</style>|<code[^>]*>.*</code>";
         
		try {
			System.out.println("Server: " + domain + ":" + PORT);
			socket = new Socket(domain, PORT);

			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String request = "GET " + resource + " HTTP/1.0\n";
			System.out.println("HTTP: " + request);

			writer.println(request);
			writer.flush();

			String line = reader.readLine();

			while (line != null) {
				if (line.length() == 0)
					break;
				line = reader.readLine();
			}
			
			line = reader.readLine();
			
			while (line != null) {
				line = reader.readLine();
				if (open) {
					buffer.append(line);
					if (closeElement(elementTagEnd, line)) {
						open = false;
						String rawHTML = buffer.toString();
						String tmpText = stripElement(elementTag, rawHTML);
						tmpText = stripEntities(tmpText);
						tmpText = stripHTML(tmpText);
						words.addAll(Arrays.asList(tmpText.replaceAll("\\W\\s", " ").toLowerCase().split("\\s+")));
					}
				}
				else {
					buffer = new StringBuffer(line);
					if (startElement(elementTagStart, line) && !closeElement(elementTagEnd, line)) {
						open = true;
					}
					else {
						String rawHTML = buffer.toString();
						String tmpText = stripElement(elementTag, rawHTML);
						tmpText = stripEntities(tmpText);
						tmpText = stripHTML(tmpText);
						for (String s: tmpText.replaceAll("\\W\\s", " ").toLowerCase().split("\\s+")) {
							s = s.replaceAll("\\W", "");
							if (s.matches("\\s*"))
							{
								continue;
							}
							words.add(s);
						}
					}
				}
			}
		}
		catch(Exception ex) {

		}
		finally {
			try {
				reader.close();
				writer.close();
				socket.close();
			}
			catch(Exception ignored) {
				// do nothing
			}
		}
         
         
		return words;

	}

	/**
	 * Downloads a webpage via a socket, removes the HTML elements, and
	 * parses the remaining text into words. Will use the first argument
	 * as the webpage URL if provided. Otherwise, will prompt the user
	 * for the URL.
	 *
	 * @param args first argument specifies URL if provided
	 */
	public static void main(String[] args) {
		String link = null;

		try {
			// read link from arguments if possible
			link = args[0];
			System.out.println("URL: " + link);
		}
		catch(Exception ex) {
			// otherwise prompt user for link to parse
			Scanner s = new Scanner(System.in);
			System.out.print("URL: ");
			link = s.nextLine();
			System.out.println();
		}

		HTMLCleaner test = new HTMLCleaner(link);
		System.out.println("Words: " + test.parseWords());

		/*
		 * URL: http://www.cs.usfca.edu/~sjengle/archived.html
		 *
		 * Words: [sophie, engle, sophie, engle, home, teaching, research,
		 * curriculum, vitae, contact, home, about, i, am, an, assistant,
		 * professor, in, the, department, of, computer, science, at, the,
		 * university, of, san, francisco, in, california, i, graduated,
		 * with, my, phd, in, computer, science, from, the, university,
		 * of, california, davis, under, the, guidance, of, matt, bishop,
		 * sean, peisert, and, many, others, at, the, seclab, i, received,
		 * my, bs, in, computer, science, from, the, university, of,
		 * nebraska, at, omaha, studying, networking, and, computer,
		 * security, with, ken, dick, and, blaine, burnham, i, also,
		 * frequently, collaborate, with, my, significant, other, sean,
		 * whalen, my, primary, research, emphasis, is, on, computer,
		 * security, especially, the, areas, of, vulnerability, analysis,
		 * insider, threat, analysis, and, electronic, voting, my,
		 * interests, also, include, information, visualization,
		 * usability, graph, theory, and, network, theory, quick, links,
		 * cs, 107, computing, mobile, apps, and, the, web, cs, 110,
		 * introduction, to, computer, science, i, cs, 212, software,
		 * development, department, of, computer, science, college, of,
		 * arts, and, sciences, university, of, san, francisco,
		 * department, of, computer, science, university, of, san,
		 * francisco, 2010, 2012, sophie, engle]
		 */
	}
}
