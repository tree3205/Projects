package cs601.webmail.pages;

import cs601.webmail.managers.*;
import cs601.webmail.misc.VerifyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.eclipse.jetty.server.Request;
import org.stringtemplate.v4.*;

public abstract class Page {
    public static Boolean debug = false;
    HttpServletRequest request;
	HttpServletResponse response;
	PrintWriter out;
    String sortType;
    String order;
	int pageNum;
//    public SearchService searchService;

    static STGroup templates = new STGroupDir("templates", '$', '$');

	public Page(HttpServletRequest request,
				HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
		try {
			out = response.getWriter();
		}
		catch (IOException ioe) {
			ErrorManager.instance().error(ioe);
		}
	}

	public void handleDefaultArgs() {
		// handle default args like page number, etc...
		String pageStr = request.getParameter("page");
		if ( pageStr!=null ) {
			pageNum = Integer.valueOf(pageStr);
		}
        sortType = request.getParameter("sort");
        order = request.getParameter("order");
	}

    public void verify() throws VerifyException {
        // handle default args like page number, etc...
        // verify that arguments make sense
        // implemented by subclass typically
        // VerifyException is a custom Exception subclass
    }

    public void getPagination() throws VerifyException {
    }

    public void generate() {
        handleDefaultArgs();
        try {
            verify(); // check args before generation
            getPagination();
            ST pageST = templates.getInstanceOf("page");
            ST headerST = generateHeader();
            ST bodyST = generateBody();
            pageST.add("head", headerST);
            pageST.add("bodyClass", getBodyClass());
            pageST.add("body", bodyST);

            // For debug use
//            if(debug) pageST.inspect();

            String page = pageST.render(); // render page
            out.println(page);


            // For debug use
            if (debug) System.out.println(page);
            if(debug) System.out.println("=============");

        } catch (VerifyException ve) {
			try {
				response.sendRedirect("/files/templates/error.html");
			}
			catch (IOException ioe) {
				ErrorManager.instance().error(ioe);
			}
		}
		catch (Exception e) {
			ErrorManager.instance().error(e);
		}
		finally {
			out.close();
		}
    }

    public abstract ST generateHeader();
    public abstract ST generateBody();
    public abstract Object getBodyClass();
}
