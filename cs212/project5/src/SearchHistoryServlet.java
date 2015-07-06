import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for showing user's query history.
 *
 * @author yxu66
 */
@SuppressWarnings("serial")
public class SearchHistoryServlet extends LoginBaseServlet {
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
				ArrayList<String> queryList = db.getQueryHistory(user);
				printInitialUI(out, queryList);
			}
			catch (IOException ex) {
				System.out.println("Unable to write response body.");
				ex.printStackTrace();
			} catch (SQLException e) {
				System.out.println("Error occurs when retrieving query history.");
				e.printStackTrace();
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
		Status status = db.clearQueryHistory(user);
		
		doGet(request, response);
	}

	private void printInitialUI(PrintWriter out, ArrayList<String> queryList) {
		assert out != null;

		out.println("<h2>Yxu66 Search History</h2>");
		out.println("<form action=\"/search_history\" method=\"post\">");
		out.println("<p><input type=\"submit\" value=\"Clear\"></p>");
		out.println("</form>");
		out.println("<hr />");
		out.println("<br />");
		for (String q : queryList) {
			out.println("<p>" + q + "</p>");
		}
		out.println("<p><a href=\"/search\">back</p>");
		out.println("<p><a href=\"/login?logout\">(logout)</a></p>");
	}
}
