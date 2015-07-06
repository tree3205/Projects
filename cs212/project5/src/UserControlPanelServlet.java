import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.pkcs11.Secmod.DbMode;

/**
 * Servlet for the {@link LoginServer} example.
 *
 * @see LoginServer
 * @author Sophie Engle
 */
@SuppressWarnings("serial")
public class UserControlPanelServlet extends LoginBaseServlet {
	/**
	 * Displays registration form. If previous registration attempt
	 * failed, displays reason why using error names to avoid cross-site
	 * scripting attacks.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String user = getUsername(request);
		
		try {
			prepareResponse("Change Password", response);

			PrintWriter out = response.getWriter();
			String error = request.getParameter("error");

			if(error != null && error.equals("nosame")) {
				out.println("<p style=\"color: red;\">New Password is different from the confirmed one</p>");
			}
			else if(error != null){
				String errorMessage = getStatusMessage(error);
				out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
			}

			printForm(out);
			finishResponse(response);
		}
		catch(IOException ex) {
			System.out.println("Unable to prepare response properly.");
			ex.printStackTrace();
		}
	}

	/**
	 * Attempts to register a new user. If successful, redirects to the
	 * login page. Otherwise, redisplays registration form and error
	 * message.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse("Change Password", response);
		
		String user = getUsername(request);

		String oldpass = request.getParameter("oldpass");
		String newpass = request.getParameter("newpass");
		String confirm = request.getParameter("confirm");
		Status status = db.authenticateUser(user, oldpass);

		try {
			if(status == Status.OK) {
				if(newpass.equals(confirm)) {
					status = db.changePassword(user, newpass);
					clearCookies(request, response);
					response.sendRedirect(response.encodeRedirectURL("/login?pwchange=true"));
				}
				else {
					String url = "/control?error=nosame";
					url = response.encodeRedirectURL(url);
					response.sendRedirect(url);
				}
			}
			else {
				String url = "/control?error=" + status.name();
				url = response.encodeRedirectURL(url);
				response.sendRedirect(url);
			}
		}
		catch(IOException ex) {
			System.out.println("Unable to redirect user. " + status);
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Prints registration form.
	 *
	 * @param out - writer for the servlet
	 */
	private void printForm(PrintWriter out) {
		assert out != null;

		out.println("<form action=\"/control\" method=\"post\">");
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Old Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"oldpass\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>New Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"newpass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Confirm Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"confirm\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p><input type=\"submit\" value=\"Submit\"></p>");
		out.println("</form>");
		out.println("<p><a href=\"/search\">back</p>");
	}
}
