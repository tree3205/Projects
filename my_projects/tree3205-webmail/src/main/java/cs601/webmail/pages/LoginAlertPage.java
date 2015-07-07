package cs601.webmail.pages;


import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginAlertPage extends Page {


    public LoginAlertPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("alertHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ST bodyST = templates.getInstanceOf("alertBody");
        bodyST.add("loginAlert", "loginAlert");
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"loginAlert\"";
    }
}
