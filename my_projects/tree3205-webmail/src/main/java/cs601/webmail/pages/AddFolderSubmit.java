package cs601.webmail.pages;

import cs601.webmail.managers.FolderModel;
import cs601.webmail.managers.User;
import cs601.webmail.managers.UserModel;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class AddFolderSubmit extends Page {
    public User u;

    public AddFolderSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        String folderName = request.getParameter("folderName");
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("user");
        try {
            new FolderModel().objects.addFolders(folderName, u);
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
