package cs601.webmail.pages;

import cs601.webmail.managers.User;
import cs601.webmail.managers.UserModel;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class RegisterSubmit extends Page {
    public User u;

    public RegisterSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        String username = request.getParameter("username");
        String webmailPwd = request.getParameter("webmailPwd");
        try {
            // New user without add email account yet
            u = new UserModel().objects.createUser(username, webmailPwd);
            if (u == null) {
                response.sendRedirect("/registerAlert");
                return;
            }
            else {
                HttpSession session = request.getSession();
                session.setAttribute("user", u);
                response.sendRedirect("/in?page=1");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
