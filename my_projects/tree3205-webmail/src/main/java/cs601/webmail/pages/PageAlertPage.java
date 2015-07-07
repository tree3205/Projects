package cs601.webmail.pages;


import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PageAlertPage extends Page {

    String type;
    public PageAlertPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        type = request.getParameter("type");
    }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("alertHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ST bodyST = templates.getInstanceOf("alertBody");
        bodyST.add("pageAlert", "pageAlert");
        bodyST.add("type", type);
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"PageAlert\"";
    }
}
