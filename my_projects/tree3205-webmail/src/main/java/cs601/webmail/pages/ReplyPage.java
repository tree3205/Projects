package cs601.webmail.pages;


import cs601.webmail.managers.Email;
import cs601.webmail.managers.EmailModel;
import cs601.webmail.managers.ErrorManager;
import cs601.webmail.managers.User;
import org.eclipse.jetty.server.Request;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public class ReplyPage extends Page {

    public Email mail;
    public Email replyMail;

    public ReplyPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        try {
            String uri = (((Request) request).getUri().toString());
            String[] l = uri.split("=");
            int mailID = Integer.valueOf(l[1]);
            mail = new EmailModel().objects.findMail(mailID);

            // Prepare the mail for reply
            int userID = mail.getUserID();
            String sender = mail.getReceiver();
            String receiver = mail.getSender();
            String cc = "";
            String bcc = "";
            String subject = "Re: "+mail.getSubject();
            String content = "\n\n\n\n\n"+"-------Reply Message-------\n"+mail.getContent();
            String date = "";
            String mailType = "";
            String uidl = "";
            String ifRead = "";
            int folderID = mail.getFolderID();
            replyMail = new Email(0, userID, sender, receiver,
                    cc, bcc, subject, content, date, mailType, uidl, ifRead, folderID, null);
        } catch (ClassNotFoundException e1) {
            ErrorManager.instance().error(e1);
        } catch (SQLException e1) {
            ErrorManager.instance().error(e1);
        }
    }

    @Override
    public ST generateHeader() {
        ST headerST = templates.getInstanceOf("homeHeader");
        return headerST;
    }

    @Override
    public ST generateBody() {
        ST bodyST = templates.getInstanceOf("sendBody");
        bodyST.add("mail", replyMail);
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"MailPage\"";
    }
}
