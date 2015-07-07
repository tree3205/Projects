package cs601.webmail.pages;

import cs601.webmail.PopServer;
import cs601.webmail.managers.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class InboxPage extends MailBoxPage {


    public InboxPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        inbox = "inbox";
        int total = 0;
        try {
            ArrayList<Email> mailList;
            ArrayList<Email> savedMailList;
            for(EmailAccount account: accountList) {
                String pop = account.getPOP();
                int port = account.getPOPPort();
                PopServer popServer = new PopServer(pop, port);
                mailList = popServer.fetchingMails(account);
                // Save mail to data base
                savedMailList = new EmailModel().objects.saveMails(mailList);
                new SearchService().updateData(savedMailList);
            }
            total = new EmailModel().objects.getTotalNum(u.getUserID(), "in", 0);
        }
        catch (SQLException e) {
                ErrorManager.instance().error(e);
        } catch (ClassNotFoundException e) {
            ErrorManager.instance().error(e);
        } catch (IOException e) {
            ErrorManager.instance().error(e);
        }
        page.setTotalRecords(total);                   // set total records
        page.setPageSize(displayNum);                  // set every page display number of records
        page.setType("in?");
    }

    public void verify() {
        if((pageNum > page.getTotalPages() && page.getTotalPages() != 0 )|| pageNum == 0) {
            try {
                response.sendRedirect("/pageAlert?type=in");
                return;
            } catch (IOException e) {
                ErrorManager.instance().error(e);
            }
        }
    }

    private ArrayList<Email> getDisplayMails() {
        ArrayList<Email> displayMails = new ArrayList<>();
        for(EmailAccount account: accountList){
            try {
                displayMails = new EmailModel().objects.retrieveDisplayMails(displayNum, pageNum,
                        account.getUserID(), "in", 0, sortType, order);
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

        // To show what current tag is from web site
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
        return "class=\"Home\"";
    }
}
