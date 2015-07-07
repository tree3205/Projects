package cs601.webmail.pages;

import cs601.webmail.managers.EmailAccount;
import cs601.webmail.managers.User;
import cs601.webmail.managers.UserModel;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;


public class AddSubmit extends Page {
    public User u;
    public AddSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
    super(request, response);
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String smtp = request.getParameter("smtp");
    String pop = request.getParameter("pop");
    int smtpport = Integer.valueOf(request.getParameter("smtpport"));
    int popport = Integer.valueOf(request.getParameter("popport"));
    HttpSession session = request.getSession();
    u = (User)session.getAttribute("user");
    EmailAccount account;
    try {
        new UserModel().objects.saveEmailAccount(u, email, password, smtp, pop, smtpport, popport);
        response.sendRedirect("/in?page=1");
        return;
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




