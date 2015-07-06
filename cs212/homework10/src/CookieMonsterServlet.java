import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CookieMonsterServlet extends BaseServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();
			prepareResponse("Cookie Monster", response);

			// Get the cookies stored on the user's browser for
			// this website.
			Map<String, String> cookies = getCookieMap(request);
			String username = cookies.get("username");
			String telephone = cookies.get("telephone");
			String greeting = cookies.get("greeting");

			writer.printf("<h2>Saved Cookies</h2>");
			
			if (username == null && telephone == null && greeting == null) {
				writer.printf("<div>No cookies saved.</div>");
			}
			else {
				username = username == null ? "unknown" : username;
				telephone = telephone == null ? "unknown" : telephone;
				greeting = greeting == null ? "unknown" : greeting;
				
				writer.printf("<table>");
				writer.printf("<tr><td>username:</td><td>" + username + "</td></tr>");
				writer.printf("<tr><td>telephone:</td><td>" + telephone + "</td></tr>");
				writer.printf("<tr><td>greeting:</td><td>" + greeting + "</td></tr>");
				writer.printf("</table>");
			}
			
			writer.printf("<h2>Edit Cookies</h2>");
			
			writer.printf("<form action=\"/\" method = \"post\">");
			writer.printf("<table>");
			writer.printf("<tr><td>username:</td><td><input type=\"textfield\" name=\"username\" /></td></tr>");
			writer.printf("<tr><td>telephone:</td><td><input type=\"textfield\" name=\"telephone\" /></td></tr>");
			writer.printf("<tr><td>greeting:</td><td><input type=\"textfield\" name=\"greeting\" /></td></tr>");
			writer.printf("</table>");
			writer.printf("<div><input type=\"checkbox\" name=\"delete\" /> Delete this cookie.</div>");
			writer.printf("<br />");
			writer.printf("<input type=\"submit\" value=\"SUBMIT\">");
			writer.printf("</form>");
			
			finishResponse(response);
		}
		catch (IOException e) {
			log.warn("Unable to properly write HTTP response.");
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		log.info("MessageServlet handling POST request.");

		try {
			String username = request.getParameter("username");
			String telephone = request.getParameter("telephone");
			String greeting = request.getParameter("greeting");
			String delete = request.getParameter("delete");
			
			if (delete == "on") {
				clearCookies(request, response);
			}
			else {
				response.addCookie(new Cookie("username", username));
				response.addCookie(new Cookie("telephone", telephone));
				response.addCookie(new Cookie("greeting", greeting));
			}

			response.sendRedirect("/");
		}
		catch (Exception e) {
			log.warn("Unable to properly write HTTP response.");
		}
	}
}
