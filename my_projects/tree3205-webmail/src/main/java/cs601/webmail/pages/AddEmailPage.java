package cs601.webmail.pages;

import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddEmailPage extends Page {

    public AddEmailPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public void verify() { }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("addHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ST bodyST = templates.getInstanceOf("addBody");
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"AddMail\"";
    }
}
