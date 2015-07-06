import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for the {@link LoginServer} example.
 *
 * @see LoginServer
 * @author Sophie Engle
 */
@SuppressWarnings("serial")
public class SearchServlet extends LoginBaseServlet {
	/**
	 * If the user is logged in, displays a welcome message. Otherwise,
	 * redirects user to the login page.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String user = getUsername(request);

		if (user != null) {
			prepareResponse("Search", response);


			try {
				PrintWriter out = response.getWriter();
				printInitialUI(out);
			}
			catch (IOException ex) {
				System.out.println("Unable to write response body.");
				ex.printStackTrace();
			}

			finishResponse(response);
		}
		else {
			try {
				response.sendRedirect("/login");
			}
			catch (Exception ex) {
				System.out.println("Unable to redirect to /login page.");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Operation not supported.
	 *
	 * @see #doGet(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String user = getUsername(request);

		if (user != null) {
			prepareResponse("Search", response);


			try {
				String query = request.getParameter("query");
				TreeSet<SearchResultFile> searchResult = Driver.partialSearcher.oneWordPartialSearch(query);
				
				Status status = db.insertQueryHistory(query, user);
				
				PrintWriter out = response.getWriter();
				printInitialUI(out);
				out.println("<br />");
				out.println("<h3>Search Results</h3>");
				out.println("<hr />");
				out.println("<br />");
				for (SearchResultFile f : searchResult)
				{
					out.println("<a href=\"/target_redirect?target=" + f.getFilePath() + "\">" + f.getFilePath() + "</a>");
					out.println("<br />");
				}
			}
			catch (IOException ex) {
				System.out.println("Unable to write response body.");
				ex.printStackTrace();
			}

			finishResponse(response);
		}
		else {
			try {
				response.sendRedirect("/login");
			}
			catch (Exception ex) {
				System.out.println("Unable to redirect to /login page.");
				ex.printStackTrace();
			}
		}

		//doGet(request, response);
	}

	private void printInitialUI(PrintWriter out) {
		assert out != null;

		out.println("<h2>Yxu66 Search</h2>");
		out.println("<a href=\"/search_history\">Search History</a>");
		out.println("<a href=\"/visited_page\">Visited Page</a>");
		out.println("<form action=\"/search\" method=\"post\">");
		out.println("<table border=\"0\"");
		out.println("\t<tr>");
		out.println("\t\t<td><input type=\"text\" name=\"query\" size=\"100\"></td>");
		out.println("\t<td><input type=\"submit\" value=\"Search\"></td>");
		out.println("\t</tr>");
		out.println("</table>");
		out.println("</form>");
		out.println("<p><a href=\"/control\">change password</p>");
		out.println("<p><a href=\"/login?logout\">(logout)</a></p>");
	}
}
