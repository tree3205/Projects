package cs601.webmail.pages;


import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterAlertPage extends Page {


    public RegisterAlertPage(HttpServletRequest request, HttpServletResponse response) {
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
        bodyST.add("registerAlert", "registerAlert");
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"registeAlert\"";
    }
}
