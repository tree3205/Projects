package cs601.webmail.pages;

import cs601.webmail.managers.Email;
import cs601.webmail.managers.ErrorManager;
import cs601.webmail.managers.SearchService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchPage extends MailBoxPage{

    public String searchItem;
    public ArrayList<Email> searchResults;
    public SearchPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        int total = 0;
        supportSort = null;
        String searchField = request.getParameter("searchField");
        searchItem = (searchField != null)? searchField: request.getParameter("item"); // for pagination url

        String[] searchList = searchItem.split(":");
        try {
            new SearchService().getDir();
            searchResults = new SearchService().search(searchList, u.getUserID());
            total = searchResults.size();
            page.setTotalRecords(total);                   // set total records
            page.setPageSize(displayNum);                  // set every page display number of records
            page.setType("search?item="+searchItem+"&");
        } catch (ParseException e) {
            ErrorManager.instance().error(e);
        } catch (IOException e) {
            ErrorManager.instance().error(e);
        } catch (ClassNotFoundException e) {
            ErrorManager.instance().error(e);
        } catch (SQLException e) {
            ErrorManager.instance().error(e);
        }

    }

    public void verify() {
        if((pageNum > page.getTotalPages() && page.getTotalPages() != 0 )|| pageNum == 0) {
            try {
                response.sendRedirect("/pageAlert?type=search?item="+searchItem+"&");
                return;
            } catch (IOException e) {
                ErrorManager.instance().error(e);
            }
        }
    }

    private List<Email> getDisplayMails() {
        List<Email> displayMails = new ArrayList<>();
        int begin = (pageNum - 1) * displayNum;
        int left = searchResults.size() - pageNum * displayNum;
        int end = (left > 0) ? pageNum * displayNum: searchResults.size();

        displayMails = searchResults.subList(begin, end);
        page.getPagination(pageNum);
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
        List<Email> displayMails = getDisplayMails();
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
        bodyST.add("ifSort", supportSort);
        if (debug) System.out.println(bodyST);
        return bodyST;
    }

    @Override
    public Object getBodyClass() {
        return "class=\"Home\"";
    }
}
