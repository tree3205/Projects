package cs601.webmail.pages;

import org.stringtemplate.v4.ST;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterPage extends Page {

    public RegisterPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public void verify() { }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("registerHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ST bodyST = templates.getInstanceOf("registerBody");
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"Register\"";
    }
}
