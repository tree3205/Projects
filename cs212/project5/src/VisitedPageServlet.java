import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for showing user's query history.
 *
 * @author yxu66
 */
@SuppressWarnings("serial")
public class VisitedPageServlet extends LoginBaseServlet {
	/**
	 * If the user is logged in, displays a welcome message. Otherwise,
	 * redirects user to the login page.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String user = getUsername(request);

		if (user != null) {
			prepareResponse("Visited Page", response);


			try {
				PrintWriter out = response.getWriter();
				ArrayList<String> urlList = db.getVisitedPage(user);
				printInitialUI(out, urlList);
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
		doGet(request, response);
	}

	private void printInitialUI(PrintWriter out, ArrayList<String> urlList) {
		assert out != null;

		out.println("<h2>Yxu66 Visited Page List</h2>");
		out.println("<hr />");
		out.println("<br />");
		for (String u : urlList) {
			out.println("<p><a href=\"" + u + "\">" + u + "</a></p>");
		}
		out.println("<p><a href=\"/search\">back</p>");
		out.println("<p><a href=\"/login?logout\">(logout)</a></p>");
	}
}
