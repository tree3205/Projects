package cs601.webmail.pages;

import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class LoginPage extends Page {

    public LoginPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public void verify() { }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("loginHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ST bodyST = templates.getInstanceOf("loginBody");
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"Login\"";
    }
}
