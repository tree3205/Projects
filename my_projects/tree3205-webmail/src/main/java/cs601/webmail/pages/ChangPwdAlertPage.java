package cs601.webmail.pages;


import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangPwdAlertPage extends Page {


    public ChangPwdAlertPage(HttpServletRequest request, HttpServletResponse response) {
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
        bodyST.add("changepwdAlert", "changepwdAlert");
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"changePwd\"";
    }
}
