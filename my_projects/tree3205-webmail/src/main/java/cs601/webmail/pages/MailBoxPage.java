package cs601.webmail.pages;


import cs601.webmail.managers.*;
import org.stringtemplate.v4.ST;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;

public class MailBoxPage extends Page{
    public String inbox;
    public String outbox;
    public String trash;
    public  String supportSort;
    public int unread;
    public User u;
    public Pagination page;
    public ArrayList<EmailAccount> accountList;
    public ArrayList<String> folderList;
    public static final int displayNum = 12;

    public MailBoxPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        inbox = null;
        outbox = null;
        trash = null;
        supportSort = "true";
        HttpSession session = request.getSession();
        u = (User)session.getAttribute("user");
        page = new Pagination();
        try {
            accountList = new UserModel().objects.getAllAcounts(u);
            for(EmailAccount account: accountList){
                unread = new EmailModel().objects.getUnreadMailNum(account.getUserID(), "in");
            }
            folderList = new FolderModel().objects.getAllFolders(u);
        } catch (Exception e) {
            ErrorManager.instance().error(e);
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
