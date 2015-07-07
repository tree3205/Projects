package cs601.webmail.pages;

import cs601.webmail.managers.ErrorManager;
import cs601.webmail.managers.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;



public class DispatchServlet extends HttpServlet {
    // use for debug
    public static Boolean debug = true;
    private Connection c;

	public static Map<String,Class> mapping = new HashMap<>();
	static {
		mapping.put("/", LoginPage.class);
		mapping.put("/login", LoginPage.class);
		mapping.put("/in", InboxPage.class);
        mapping.put("/register", RegisterPage.class);
        mapping.put("/loginSubmit", LoginSubmit.class);
        mapping.put("/registerSubmit", RegisterSubmit.class);
        mapping.put("/logout", LogoutPage.class);
        mapping.put("/loginAlert", LoginAlertPage.class);
        mapping.put("/registerAlert", RegisterAlertPage.class);
        mapping.put("/sendSubmit", SendSubmit.class);
        mapping.put("/sendMail", SendMailPage.class);
        mapping.put("/sendAlert", ChangPwdAlertPage.class);
        mapping.put("/addMail", AddEmailPage.class);
        mapping.put("/addSubmit", AddSubmit.class);
        mapping.put("/mail", MailPage.class);
        mapping.put("/deleteMail", DeleteSubmit.class);
        mapping.put("/trash", TrashPage.class);
        mapping.put("/empty", EmptySubmit.class);
        mapping.put("/clearMail", ClearOneMail.class);
        mapping.put("/out", OutboxPage.class);
        mapping.put("/reply", ReplyPage.class);
        mapping.put("/forward", ForwardPage.class);
        mapping.put("/changepwd", ChangePwdPage.class);
        mapping.put("/changepwdSubmit", ChangePwdSubmit.class);
        mapping.put("/changePwdAlert", ChangPwdAlertPage.class);
        mapping.put("/pageAlert", PageAlertPage.class);
        mapping.put("/addFolder", AddFolderPage.class);
        mapping.put("/addFolderSubmit", AddFolderSubmit.class);
        mapping.put("/folder", FolderPage.class);
        mapping.put("/saveTo", SaveToSubmit.class);
        mapping.put("/search", SearchPage.class);
        mapping.put("/error", ErrorPage.class);
        mapping.put("/download", DownloadPage.class);
    }

	public void doGet(HttpServletRequest request,
					  HttpServletResponse response)
		throws ServletException, IOException
	{
        response.setContentType("text/html;charset=UTF-8");
		String uri = request.getRequestURI();
        if (debug) System.out.println("get:" + uri);

        HttpSession session = request.getSession();
        User u = (User)session.getAttribute("user");
        ArrayList<String> uris = new ArrayList<>();
        uris.add("/login");
        uris.add("/register");
        uris.add("/loginAlert");
        uris.add("/registerAlert");
        uris.add("/logout");
        uris.add("/error");
        if (uri.equals("/download")) {
            AttachPage p = createAttachPage(uri, request, response);
            if (p == null) {
                response.sendRedirect("/error");
                return;
            }
            p.generate();
        }
        else if (!uris.contains(uri)) {
            if (u == null) {
                response.sendRedirect("/login");
                return;
            }
            else {
                Page p = createPage(uri, request, response);
                if ( p == null ) {
                    response.sendRedirect("/error");
                    return;
                }
                p.generate();
            }
        }
        else {
            Page p = createPage(uri, request, response);
            if ( p == null ) {
                response.sendRedirect("/error");
                return;
            }
            p.generate();
        }
	}

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException
    {
        String uri = request.getRequestURI();
        if (debug) System.out.println("post:" + uri);

        ArrayList<String> uris = new ArrayList<>();
        uris.add("/loginSubmit");
        uris.add("/registerSubmit");

        HttpSession session = request.getSession();
        User u = (User)session.getAttribute("user");

        if (uris.contains(uri)) {
            if (u == null) {
                Page p = createPage(uri, request, response);
                if (p == null) {
                    response.sendRedirect("/error");
                    return;
                }
                p.generate();
            } else {
                response.sendRedirect("/in?page=1");
                return;
            }
        }
        else {
            if (u != null) {
                Page p = createPage(uri, request, response);
                if ( p == null ) {
                    response.sendRedirect("/error");
                    return;
                }
                p.generate();
            }
            else {
                response.sendRedirect("/login");
                return;
            }
        }
    }

    public Page createPage(String uri,
						   HttpServletRequest request,
						   HttpServletResponse response)
	{
        Class pageClass = mapping.get(uri);
		try {
			Constructor<Page> ctor = pageClass.getConstructor(HttpServletRequest.class,
															  HttpServletResponse.class);
			return ctor.newInstance(request, response);
		}
		catch (Exception e) {
            System.out.println(e.getCause());
            ErrorManager.instance().error(e);
		}
		return null;
	}

    public AttachPage createAttachPage(String uri,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        Class pageClass = mapping.get(uri);
        try {
            Constructor<AttachPage> ctor = pageClass.getConstructor(HttpServletRequest.class,
                    HttpServletResponse.class);
            return ctor.newInstance(request, response);
        } catch (Exception e) {
            ErrorManager.instance().error("Request not found: " + uri);
            ErrorManager.instance().error(e);
        }
        return null;
    }
}
