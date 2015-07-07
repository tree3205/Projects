package cs601.webmail.pages;

import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangePwdPage extends Page {

    public ChangePwdPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public void verify() { }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("changepwdHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ST bodyST = templates.getInstanceOf("changepwdBody");
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"Changepwd\"";
    }
}
