package cs601.webmail.pages;

import cs601.webmail.managers.ErrorManager;
import cs601.webmail.managers.User;
import cs601.webmail.managers.UserModel;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class ChangePwdSubmit extends Page {

    public ChangePwdSubmit(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        String currentPwd = request.getParameter("currentPWD");
        String newPwd = request.getParameter("newPWD");
        HttpSession session = request.getSession();
        User u = (User)session.getAttribute("user");
        try {
            boolean success = new UserModel().changePwd(u, currentPwd, newPwd);
            if(success) {
                response.sendRedirect("/in?page=1");
                return;
            }
            else {
                response.sendRedirect("/changePwdAlert");
                return;
            }
        } catch (SQLException e) {
            ErrorManager.instance().error(e);
        } catch (ClassNotFoundException e) {
            ErrorManager.instance().error(e);
        } catch (IOException e) {
            ErrorManager.instance().error(e);
        }
    }

    public void verify() { }

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
