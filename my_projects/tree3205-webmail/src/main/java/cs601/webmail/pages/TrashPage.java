package cs601.webmail.pages;

import cs601.webmail.managers.*;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class TrashPage extends MailBoxPage {

	public TrashPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        trash = "trash";
        int total = 0;
        try {
            total = new EmailModel().objects.getTotalNum(u.getUserID(), "trash", 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        page.setTotalRecords(total);                   // set total records
        page.setPageSize(displayNum);                  // set every page display number of records
        page.setType("trash?");
    }

    public void verify() {
        if((pageNum > page.getTotalPages() && page.getTotalPages() != 0 )|| pageNum == 0) {
            try {
                response.sendRedirect("/pageAlert?type=trash");
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
                displayMails = new EmailModel().objects.retrieveDisplayMails(displayNum, pageNum,
                        account.getUserID(), "trash", 0, sortType, order);
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
        // Default display inbox view(15 mails per page)
        ST bodyST = templates.getInstanceOf("homeBody");
        ArrayList<Email> displayMails = getDisplayMails();

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
        bodyST.add("ifSort", supportSort);

        bodyST.add("folderList", folderList);
        if (debug) System.out.println(bodyST);
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"Trash\"";
    }
}
