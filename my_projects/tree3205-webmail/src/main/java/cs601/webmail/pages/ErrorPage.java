package cs601.webmail.pages;

import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorPage extends Page{
    public ErrorPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public ST generateHeader() {
//        String favicon = "<link rel=\"shortcut icon\" href=\"https://1000hz.github.io/bootstrap-validator/assets/ico/favicon.png\">";
//        ST str = new ST(favicon);
//        return str;
        return null;
    }

    @Override
    public ST generateBody() {
        out.println("<h1>");
        out.println("Error Happened!");
        out.println("</h1>");
        return null;
    }

    @Override
    public Object getBodyClass() {
        return null;
    }
}
