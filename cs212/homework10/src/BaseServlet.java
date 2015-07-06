import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {

	// Will be using the same logger as Jetty.
	protected static Logger log = Log.getLogger(BaseServlet.class);

	// Will add the header HTML to the HTTP response for every page.
	protected void prepareResponse(String title, HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();

			writer.printf("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n\n");
			writer.printf("<html>\n\n");
			writer.printf("<head>\n");
			writer.printf("\t<title>%s</title>\n", title);
			writer.printf("\t<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">\n");
			writer.printf("</head>\n\n");
			writer.printf("<body>\n\n");
		}
		catch (IOException ex) {
			log.warn("Unable to prepare HTTP response.");
			return;
		}
	}

	// Will add the footer HTML to the HTTP response for every page.
	protected void finishResponse(HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();

			writer.printf("\n");
			writer.printf("<p style=\"font-size: 10pt; font-style: italic;\">");
			writer.printf("Last updated at %s.", getDate());
			writer.printf("</p>\n\n");

			writer.printf("</body>\n");
			writer.printf("</html>\n");

			writer.flush();

			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		}
		catch (IOException ex) {
			log.warn("Unable to finish HTTP response.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	// Helper method for getting the current date as a string.
	protected String getDate() {
		String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	// Helper method for getting a name=value map of all the cookies
	// included in the HTTP request.
	protected Map<String, String> getCookieMap(HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<String, String>();

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}

		return map;
	}

	// Helper method for removing all of the cookies stored on the
	// user's browser for this website.
	protected void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();

		if(cookies == null) {
			return;
		}

		for(Cookie cookie : cookies) {
			// Set the age to 0 so that the cookie
			// will be removed by the browser.
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	// Remove a specific cookie if it exists.
	protected void clearCookie(String cookieName, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	// Show the cookies stored with the HTTP request.
	protected void debugCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if(cookies == null) {
			log.info("Saved Cookies: []");
		}
		else {
			String[] names = new String[cookies.length];

			for(int i = 0; i < names.length; i++) {
				names[i] = String.format("(%s, %s, %d)",
						cookies[i].getName(),
						cookies[i].getValue(),
						cookies[i].getMaxAge());
			}

			log.info("Saved Cookies: " + Arrays.toString(names));
		}
	}
}
