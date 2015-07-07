package cs601.webmail.pages;

import cs601.webmail.managers.Attachment;
import cs601.webmail.managers.EmailModel;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class DownloadPage extends AttachPage{

    public DownloadPage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public void download() throws IOException {
        int attachID = Integer.valueOf(request.getParameter("att"));
        Attachment attach = null;
        try {
            attach = new EmailModel().objects.findAttachment(attachID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String filePath = attach.getFileContent();
        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);
//        String content =  new String(Files.readAllBytes(Paths.get(filePath)));

//        response.setContentType("application/download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "filename=" + attach.getFileName());
        response.setContentLength((int) downloadFile.length());
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inStream.close();
        outStream.close();
    }
}
