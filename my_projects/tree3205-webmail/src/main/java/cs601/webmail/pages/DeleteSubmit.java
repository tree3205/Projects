package cs601.webmail.pages;

import cs601.webmail.managers.EmailModel;
import cs601.webmail.managers.User;
import cs601.webmail.managers.UserModel;
import org.eclipse.jetty.server.Request;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class DeleteSubmit extends Page{

    public DeleteSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        try {
            int mailID = Integer.valueOf(request.getParameter("id"));
            new EmailModel().deleteMail(mailID);
            response.sendRedirect("/in?page=1");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    @Override
    public ST generateHeader() {
        return null;
    }

    @Override
    public ST generateBody() {
        return null;
    }

    @Override
    public Object getBodyClass() {
        return null;
    }
}
