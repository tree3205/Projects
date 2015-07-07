package cs601.webmail.pages;

import cs601.webmail.managers.SearchService;
import cs601.webmail.managers.User;
import cs601.webmail.managers.UserModel;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class LoginSubmit extends Page{
    public User u;

    public LoginSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            u = new UserModel().objects.authenticate(email, password);
            if (u == null) {
                response.sendRedirect("/loginAlert");
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
