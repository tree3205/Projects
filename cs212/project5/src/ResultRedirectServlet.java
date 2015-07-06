import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for showing user's query history.
 *
 * @author yxu66
 */
@SuppressWarnings("serial")
public class ResultRedirectServlet extends LoginBaseServlet {
	/**
	 * If the user is logged in, displays a welcome message. Otherwise,
	 * redirects user to the login page.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String user = getUsername(request);

		if (user != null) {
			prepareResponse("Redirect", response);


			try {
				String url = request.getParameter("target");
				db.insertVisitedPage(url, user);
				response.sendRedirect(url);
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
		doGet(request, response);
	}
}
