package cs601.webmail.pages;

import cs601.webmail.managers.FolderModel;
import cs601.webmail.managers.User;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;


public class SendMailPage extends Page {
    public User u;
    public SendMailPage(HttpServletRequest request, HttpServletResponse response) {

        super(request, response);
        HttpSession session = request.getSession();
        u = (User)session.getAttribute("user");
    }

    public void verify() { }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("homeHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ArrayList<String> folderList = new ArrayList<>();
        try {
            folderList = new FolderModel().objects.getAllFolders(u);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ST bodyST = templates.getInstanceOf("sendBody");
        bodyST.add("folderList", folderList);
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"SendMail\"";
    }
}
