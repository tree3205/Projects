package cs601.webmail.pages;

import cs601.webmail.managers.*;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class FolderPage extends MailBoxPage {
    public String folderName;
    public int folderID;
    public static final int displayNum = 12;

    public FolderPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        folderName = request.getParameter("type");
        try {
            folderID = new FolderModel().objects.getFolderID(folderName, u.getUserID());
            int total = new EmailModel().objects.getTotalNumOfFolderRecords(u.getUserID(), folderID);
            page.setTotalRecords(total);                   // set total records
            page.setPageSize(displayNum);                  // set every page display number of records
            page.setType("folder?type="+folderName+"&");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void verify() {
        if((pageNum > page.getTotalPages() && page.getTotalPages() != 0 )|| pageNum == 0) {
            try {
                response.sendRedirect("/pageAlert?type="+folderName);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Email> getDisplayMails() {
        ArrayList<Email> displayMails = new ArrayList<>();
        for(EmailAccount account: accountList){
            try {
                displayMails = new EmailModel().objects.getDisplayMailsInFolder(displayNum, pageNum,
                        account.getUserID(), folderID, sortType, order);
                page.getPagination(pageNum);    // changed here
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return displayMails;
    }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("homeHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ArrayList<String> folderList = new ArrayList<>();
        ST bodyST = templates.getInstanceOf("homeBody");
        ArrayList<Email> displayMails = getDisplayMails();
        try {
            folderList = new FolderModel().objects.getAllFolders(u);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // To show what current tag is from web site
        String inbox = null;
        String outbox = null;
        String trash = null;
        String folder = folderName;

        bodyST.add("mails", displayMails);
        bodyST.add("inbox", inbox);
        bodyST.add("outbox", outbox);
        bodyST.add("trash", trash);
        if (unread == 0) {
            bodyST.add("unread", null);
        }
        bodyST.add("unread", unread);
        bodyST.add("page", page);
        bodyST.add("currentPage", pageNum);
        bodyST.add("sortBy", sortType);
        bodyST.add("order", order);
        bodyST.add("folderList", folderList);
        bodyST.add("folderName", folder);
        if (debug) System.out.println(bodyST);
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"Folder\"";
    }
}
