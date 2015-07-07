package cs601.webmail.pages;

import cs601.webmail.managers.ErrorManager;
import cs601.webmail.misc.VerifyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public abstract class AttachPage {
    HttpServletRequest request;
    HttpServletResponse response;
    OutputStream out;

    public AttachPage(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        try {
            out = response.getOutputStream();
        } catch (IOException ioe) {
            ErrorManager.instance().error(ioe);
        }
    }

    public void generate() {
        try {
            download();
        } catch (VerifyException ve) {
            try {
                response.sendRedirect("/files/error.html");
            } catch (IOException ioe) {
                ErrorManager.instance().error(ioe);
            }
        } catch (Exception e) {
            ErrorManager.instance().error(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                ErrorManager.instance().error(e);
            }
        }
    }

    public abstract void download() throws IOException;
}
