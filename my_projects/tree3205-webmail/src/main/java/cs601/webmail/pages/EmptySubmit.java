package cs601.webmail.pages;

import cs601.webmail.managers.*;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmptySubmit extends Page {
    public User u;
    public ArrayList<EmailAccount> accountList;

    public EmptySubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
    super(request, response);
        try {
            HttpSession session = request.getSession();
            u = (User)session.getAttribute("user");
            accountList = new UserModel().objects.getAllAcounts(u);
            ArrayList<Integer> clearedMailList = new EmailModel().objects.emptyTrash(accountList);
            new SearchService().clear(clearedMailList);

            response.sendRedirect("/trash?page=1");
            return;
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




