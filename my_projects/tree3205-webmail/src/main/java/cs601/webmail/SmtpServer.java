package cs601.webmail;

import cs601.webmail.managers.Email;
import cs601.webmail.managers.EmailAccount;
import cs601.webmail.pages.DispatchServlet;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SmtpServer {
    public BufferedReader in;
    private BufferedReader sockin;

    public static Boolean debug = false;
    private Socket client;
    private PrintWriter sockout;
    private boolean SSL = false;


    public SmtpServer(String smtp, int port)throws IOException {
            connect(smtp, port);
            sockin = new BufferedReader(new InputStreamReader(client.getInputStream()));
            sockout = new PrintWriter(client.getOutputStream(), true);
    }

    public void connect(String host, int port) throws IOException {
        if (port == 465) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            client = sslsocketfactory.createSocket(host, port);
            SSL = true;
        } else if (port == 25) {
            client = new Socket(host, port);
            SSL = false;
        }
        if (debug && isConnected()) System.out.println("Connected to the host");
    }

    public boolean isConnected() {
        return client != null;
    }

    public void disconnect() throws IOException {
        if (!isConnected())
            throw new IllegalStateException("Not connected to a host");
        client.close();
        sockin = null;
        sockout = null;
        if (debug)
            System.out.println("Disconnected from the host");
    }

    public void sendCommand(String str) throws IOException {
        sockout.println(str);
        if (debug) System.out.println(str);
    }

    public void readResponseLine() throws IOException {
        if (debug) {
            System.out.println("SERVER: " + sockin.readLine());
        } else {
            // Save to log file?
            if (sockin.readLine().startsWith("-ERR")) {
                throw new RuntimeException("Server has returned an error: " + sockin.readLine().replaceFirst("-ERR ", ""));
            }
        }
    }

    public void sendHeader(String sender, String recipient) throws IOException {
        sendCommand("MAIL FROM:" + "<" + sender + ">");
        readResponseLine();
        sendCommand("RCPT TO:" + "<" + recipient + ">");
        readResponseLine();
    }

    public void sendContent(String sender, String recipient,
              String cc, String bcc, String subject, String content,
              String encodedContent, String fileType, String fileName) throws IOException {
        // Write mail
        sendCommand("DATA");
        readResponseLine();
        sendCommand("From: " + sender);
        sendCommand("To: " + recipient);
        sendCommand("cc: " + cc);
        sendCommand("Bcc: " + bcc);
        sendCommand("Subject: " + subject);
        sendCommand("MIME-Version: 1.0");
        sendCommand("Content-Type: multipart/mixed; boundary=\"KkK170891tpbkKk__FV_KKKkkkjjwq\"\n");
        sendCommand("\n\n"); // should has, otherwise cannot get content correctly
        sendCommand("--KkK170891tpbkKk__FV_KKKkkkjjwq");
        sendCommand("\n\n");
        sendCommand(content);
        sendCommand("\n\n");
        sendCommand("--KkK170891tpbkKk__FV_KKKkkkjjwq");
        sendCommand("Content-Transfer-Encoding:base64");
        sendCommand("Content-Type:application/octet-stream;name=\""+ fileName +"\"");
        sendCommand("Content-Disposition:attachment;filename=\"" + fileName + "\"");
        sendCommand("\n\n");
        sendCommand(encodedContent);
        sendCommand("\n\n");
        sendCommand("--KkK170891tpbkKk__FV_KKKkkkjjwq--");
        sendCommand("\r\n.\r\n");
        readResponseLine();
    }

    public void sendOutMail(String sender, String recipient, String CCs,
                            String Bccs, String subject, String content,
                            String encodedContent, String fileType, String fileName) throws IOException {
        // Send mail to recipient
        if (recipient.length()!= 0) {
            String[] recipientsList = recipient.split(",");
            for (int i=0; i < recipientsList.length; i++) {
                sendHeader(sender, recipient);
                sendContent(sender, recipient, CCs, "", subject, content, encodedContent, fileType, fileName);
            }
        }

        // Send mail to cc address
        if (CCs.length()!= 0) {
            String[] ccList = CCs.split(",");
            for (int i=0; i < ccList.length; i++) {

                sendHeader(sender, ccList[i]);
                sendContent(sender, recipient, CCs, "", subject, content, encodedContent, fileType, fileName);
            }
        }

        // Send mail to bcc address
        if (Bccs.length()!= 0) {
            String[] bccList = Bccs.split(",");
            for (int i=0; i < bccList.length; i++) {
                sendHeader(sender, bccList[i]);
                sendContent(sender, recipient, CCs, Bccs, subject, content, encodedContent, fileType, fileName);
            }
        }
    }

    public void sendMail(EmailAccount account, String sender, String recipient, String CC,
                            String Bccs, String subject, String content, String encodedContent,
                            String fileType, String fileName) throws IOException {
        String user = new String(Base64.encodeBase64(account.getEmail().getBytes()));
        String pwd = new String(Base64.encodeBase64(account.getPassword().getBytes()));

        if (client.isConnected()) {
            // read response of hand shake for Gmail(SSL)
            if (SSL) {
                // Hand shake
                sendCommand("HELO");
                readResponseLine();
                readResponseLine();
                // Authentication
                sendCommand("AUTH LOGIN");
                readResponseLine();
                sendCommand(user);
                readResponseLine();
                sendCommand(pwd);
                readResponseLine();
                sendOutMail(sender, recipient, CC, Bccs, subject, content, encodedContent, fileType, fileName);
            } else {
                // Hand shake
                sendCommand("HELO ");
                readResponseLine();
                readResponseLine();
                sendOutMail(sender, recipient, CC, Bccs, subject, content, encodedContent, fileType, fileName);
            }
            sendCommand("QUIT");
            readResponseLine();
        }
        disconnect();
    }
}

