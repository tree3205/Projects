package cs601.webmail.pages;

import cs601.webmail.managers.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.eclipse.jetty.server.Request;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClearOneMail extends Page {
    public User u;
    public ArrayList<EmailAccount> accountList;

    public ClearOneMail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        try {
            int mailID = Integer.valueOf(request.getParameter("id"));
            new EmailModel().clearOneMail(mailID);
            new SearchService().deleteData(mailID);
            response.sendRedirect("/trash?page=1");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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




