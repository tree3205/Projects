package cs601.webmail.pages;

import cs601.webmail.managers.*;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class OutboxPage extends MailBoxPage {

    public OutboxPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        outbox  = "outbox";
        int total = 0;
        try {
            total = new EmailModel().objects.getTotalNum(u.getUserID(), "out", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        page.setTotalRecords(total);                   // set total records
        page.setPageSize(displayNum);                  // set every page display number of records
        page.setType("out?");
    }

    public void verify() {
        if((pageNum > page.getTotalPages() && page.getTotalPages() != 0 )|| pageNum == 0) {
            try {
                response.sendRedirect("/pageAlert?type=out");
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
                        account.getUserID(), "out", 0, sortType, order);
                page.getPagination(pageNum);    // changed here
            } catch (SQLException e) {
                ErrorManager.instance().error(e);
            } catch (ClassNotFoundException e) {
                ErrorManager.instance().error(e);
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
        return "class=\"Outbox\"";
    }
}
