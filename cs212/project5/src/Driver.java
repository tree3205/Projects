//:object/Driver.java
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Test the project3
 * 
 * @author yxu66
 */
public class Driver 
{
	public static PartialSearch partialSearcher;

	/**
	 * Build an invertedIndexMap based on given directory, do partial search on the
	 * invertedIndexMap and output the result to a certain file.
	 * Output the specific invertedIndexMap of provided directory to a certain file if required.
	 * 
	 * @param args the argument that input to test the program.
	 */
	public static void main(String[] args) 
	{	
		ArgumentParser argParser = new ArgumentParser(args);
		if (argParser.hasFlag("-u") && argParser.hasFlag("-p") && argParser.hasFlag("-t"))
		{
			String rootURL = argParser.getValue("-u");
			URL root;
			try 
			{
				root = new URL(rootURL);
				
				int port = Integer.parseInt(argParser.getValue("-p"));

				int threads = Integer.parseInt(argParser.getValue("-t"));

				InvertedIndex wordsMap = new InvertedIndex();

				InvertedIndexHTMLBuilder mapBuilder = new InvertedIndexHTMLBuilder(wordsMap, threads, 50);
				mapBuilder.parseURL(root);

				partialSearcher = new PartialSearch(wordsMap, threads);

				if (argParser.hasFlag("-i"))
				{
					wordsMap.outputToFile("invertedindex.txt");
				}
				
				if (argParser.hasFlag("-q"))
				{
					String queryFilePath = argParser.getValue("-q");
					File queryFile = new File(queryFilePath);
					partialSearcher.partialSearch(queryFile, "searchresults.txt");
				}
                
				Server server = new Server(port);
				
				ServletHandler sh = new ServletHandler();
				server.setHandler(sh);
				
				sh.addServletWithMapping(LoginRedirectServlet.class, "/");
				sh.addServletWithMapping(LoginRegisterServlet.class, "/register");
				sh.addServletWithMapping(LoginUserServlet.class, "/login");
				sh.addServletWithMapping(UserControlPanelServlet.class, "/control");
				sh.addServletWithMapping(SearchServlet.class, "/search");
				sh.addServletWithMapping(SearchHistoryServlet.class, "/search_history");
				sh.addServletWithMapping(VisitedPageServlet.class, "/visited_page");
				sh.addServletWithMapping(ResultRedirectServlet.class, "/target_redirect");


				server.start();
				server.join();

			} 
			catch (MalformedURLException e1)
			{
				System.out.println("The URL is malformed.");
			}
			catch (Exception e)
			{
				System.out.println("<threads> should be an Integer.");
			}
			
		}
		else
		{
			System.out.println("Usage: java -cp project5.jar Driver -u <seed> -p <port> -t <threads> [-i -q <queryfile>]");
		}
	}

}
