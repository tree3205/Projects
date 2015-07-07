package cs601.webmail.pages;

import cs601.webmail.managers.FolderModel;
import cs601.webmail.managers.User;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class SaveToSubmit extends Page {
    public User u;

    public SaveToSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        String folderName = request.getParameter("type");
        int mailID = Integer.valueOf(request.getParameter("mailID"));

        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("user");

        try {
            int folderID = new FolderModel().objects.getFolderID(folderName, u.getUserID());
            new FolderModel().objects.saveToFolder(folderID, mailID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        response.sendRedirect("/in?page=1");
        return;

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
