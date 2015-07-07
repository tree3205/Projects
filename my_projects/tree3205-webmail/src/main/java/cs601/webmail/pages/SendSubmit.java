package cs601.webmail.pages;

import cs601.webmail.SmtpServer;
import cs601.webmail.managers.EmailAccount;
import cs601.webmail.managers.ErrorManager;
import cs601.webmail.managers.UserModel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SendSubmit extends Page {
//    public Email email;
    public boolean debug = true;
    public HashMap<String, String> requestParameters;
    public SendSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(request, response);
        // Check that we have a file upload request
        requestParameters = new HashMap<>();
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            doPost(request, response);
        }
    }

    private void doPost(HttpServletRequest request, HttpServletResponse response) {
        String encodedContent = "";
        String fileType = "";
        String fileName = "";

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory(10240, new File("uploadFile"));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request
        try {
            List<FileItem> items = upload.parseRequest(request);
            // Process the uploaded items
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();
                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                if (item.isFormField()) {
                    processFormField(item);
                }
                // Process form file field (input type="file").
                else {
                    encodedContent = processUploadedFile(item);
                    fileType = item.getContentType();
                    fileName = item.getName();
                }
            }

            String sender = requestParameters.get("sender");
            String receiver = requestParameters.get("receiver");
            String cc = requestParameters.get("cc");
            String bcc = requestParameters.get("bcc");
            String subject = requestParameters.get("subject");
            String content = requestParameters.get("content");

            //attachment
            EmailAccount account = new UserModel().objects.findAccount(sender);
            String smtp = account.getSMTP();
            int port = account.getSMTPPort();
            SmtpServer smtpServer = new SmtpServer(smtp, port);
            smtpServer.sendMail(account, sender, receiver, cc,
                     bcc, subject, content, encodedContent, fileType, fileName);
            response.sendRedirect("/in?page=1");
            return;
        } catch (FileUploadException e) {
            ErrorManager.instance().error(e);
        } catch (IOException e) {
            ErrorManager.instance().error(e);
        } catch (Exception e) {
            ErrorManager.instance().error(e);
        }
    }

    // parse data to base64 and then store it in db.
    private String processUploadedFile(FileItem item) throws IOException {
        InputStream uploadedStream = item.getInputStream();
        Base64InputStream base64InputStream = new Base64InputStream(uploadedStream, true, -1, null);
        byte[] retr = IOUtils.toByteArray(base64InputStream);
        String base64Content = new String(retr);

        uploadedStream.close();
        base64InputStream.close();
        return base64Content;
    }

    private void processFormField(FileItem item) {
        String fieldName = item.getFieldName();
        String fieldValue = item.getString();
        requestParameters.put(fieldName, fieldValue);
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

    public static void main(String[] args) {
        String s = "String to encode/decode in Base64 using streams";

        ByteArrayOutputStream collect = new ByteArrayOutputStream();
        Base64OutputStream b64os = new Base64OutputStream(collect);

        try {
            b64os.write(s.getBytes());
            b64os.close();
        } catch (IOException e) {
            ErrorManager.instance().error(e);
        }

        byte[] ba = collect.toByteArray();
        String coded = new String(ba);

        InputStream is = new ByteArrayInputStream(ba);
        Base64InputStream b64is = new Base64InputStream(is, true, -1, null);

        byte[] retr = new byte[0];
        try {
            retr = IOUtils.toByteArray(b64is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String encodedContent = new String(Base64.encodeBase64(ba));
        System.out.println(encodedContent);

        System.out.println("Coded     : " + coded);
        System.out.println("Expected  : " + s);
        System.out.println("Retrieved : " + new String(retr));



    }
}
