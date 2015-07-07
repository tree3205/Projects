package cs601.webmail.pages;


import cs601.webmail.managers.*;
import org.eclipse.jetty.server.Request;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MailPage extends Page {

    public Email mail;
    public int unread;
    public User u;
    public int mailID;
    public ArrayList<EmailAccount> accountList;
    private final String FILES_DIR = "uploadFile";

    public MailPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        try {
            HttpSession session = request.getSession();
            u = (User)session.getAttribute("user");

            mailID = Integer.valueOf(request.getParameter("id"));
            mail = new EmailModel().objects.findMail(mailID);
            new EmailModel().objects.updateStatus(mailID, mail);

            accountList = new UserModel().objects.getAllAcounts(u);
            for(EmailAccount account: accountList) {
                unread = new EmailModel().objects.getUnreadMailNum(account.getUserID(), "in");
            }
        } catch (ClassNotFoundException e) {
            ErrorManager.instance().error(e);
        } catch (SQLException e) {
            ErrorManager.instance().error(e);
        } catch (Exception e) {
            ErrorManager.instance().error(e);
        }
    }

    // inner class for stringtemplate use easily
    class SaveToLink {
        String folder;
        int mailID;

        SaveToLink(String folder, int mailID) {
            this.folder = folder;
            this.mailID = mailID;
        }

        public String getFolder(){return folder;}
        public int getMailID(){return mailID;}
    }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("homeHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ArrayList<String> folderList = new ArrayList<>();
        ArrayList<SaveToLink> folderLinkList = new ArrayList<>();
        String folderName=null;
        try {
            folderList = new FolderModel().objects.getAllFolders(u);
            for(String folder: folderList) {
                SaveToLink link = new SaveToLink(folder, mailID);
                folderLinkList.add(link);
            }
        } catch (SQLException e) {
            ErrorManager.instance().error(e);
        } catch (ClassNotFoundException e) {
            ErrorManager.instance().error(e);
        }
        ST bodyST = templates.getInstanceOf("mailBody");
        // If type == trash, then delete will empty mail
        // else delete will just move mail to trash
        String empty=null;
        if("trash".equals(mail.getType())) {
            empty ="true";
        }

        if(mail.getFolderID()!=0) {
            try {
                folderName = new FolderModel().objects.getFolderName(mail.getFolderID());
//                System.out.println("****************"+ folderName);
            } catch (SQLException e) {
                ErrorManager.instance().error(e);
            } catch (ClassNotFoundException e) {
                ErrorManager.instance().error(e);
            }
        }
        // For display content
        String[] tmpList = mail.getContent().split("\n");
        List<String> contents = Arrays.asList(tmpList);
        //process attachment
        ArrayList<Attachment> attachList = mail.getAttachment();

        bodyST.add("mail", mail);
        bodyST.add("empty", empty);
        bodyST.add("contents", contents);
        bodyST.add("unread", unread);
        bodyST.add("folderList", folderList);
        bodyST.add("folderLinkList", folderLinkList);
        bodyST.add("folderName", folderName);
        bodyST.add("attachList", attachList);
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"MailPage\"";
    }
}
