import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by treexy1230 on 4/21/15.
 */
class SMTPDemo {
    // Note: SMTPServer and receiver should be the same server
    public static Socket client;    // Should be singleton??

    // SSL for gmail
//    private static String receiverSSL = "yxu66mail@gmail.com";
//    private static String passwordSSL = "onlineyxu66";
    private static String passwordSSL = "mailyxu66";
    private static String receiverSSL = "yxu66@dons.usfca.edu";
    private static String senderSSL = "yxu66mail@gmail.com";

    // TCP for usual
    private static String receiverTCL = "yxu66@dons.usfca.edu";
    private static String passwordTCL = "123";
    private static String senderTCL = "yxu66mail@gmail.com";


    private static String cc = "treexy1230@gmail.com";
    private static String bcc = "yxu@snaplogic.com";
    private static String SMTPServerSSL = "smtp.gmail.com";
    private static String SMTPServerTCL = "smtp.usfca.edu";
//    private static String SMTPServerTCL = "smtp.usfca.edu";

    private static String s = new String(Base64.encodeBase64(senderSSL.getBytes()));
    private static String p = new String(Base64.encodeBase64(passwordSSL.getBytes()));

    private static BufferedReader sockin;
    private static PrintWriter sockout;
    private static boolean debug = true;
    private static final int SMTPPort_GmailSSL = 465;
    private static final int SMTPPort_GmailTLP = 587;
    private static final int SMTPPort = 25;
    public static String content = "Test for attachment 1 !!!!!!";
    public boolean SSL = true;
    public static String subject = "Test attachment";
    public StringBuilder sb = new StringBuilder();

    public SMTPDemo(String host, int port) {
        if(this.SSL)
        {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try {
                client = sslsocketfactory.createSocket(host, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                client = new Socket(host, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() throws IOException {
        sockin = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sockout = new PrintWriter(client.getOutputStream(), true);
    }

    public boolean isConnected() {
        return client != null;
    }

    private void readResponseLine() throws IOException {
        if (debug) {
            System.out.println("SERVER: "+ sockin.readLine());
        }
        else {
            // Save to log file?
            if (sockin.readLine().startsWith("-ERR")) {
                throw new RuntimeException("Server has returned an error: " + sockin.readLine().replaceFirst("-ERR ", ""));
            }
        }
    }

    public void sendCommend(String str) {
        sockout.println(str);
        if(debug) System.out.println(str);
    }

    public void sendMail(String sender, String recipient, String CCs,
                         String Bccs, String subject, String content) throws IOException {

        // Send mail to recipient
        sendHeader(sender, recipient);
        sendContent(sender, recipient, CCs, "", subject, content);

        // Send mail to cc address

        if (CCs.length()!= 0) {
            String[] ccList = CCs.split(",");
            for (int i=0; i < ccList.length; i++) {

                sendHeader(sender, ccList[i]);
                sendContent(sender, recipient, CCs, "", subject, content);
            }
        }

        // Send mail to bcc address
        if (Bccs.length()!= 0) {
            String[] bccList = Bccs.split(",");
            for (int i=0; i < bccList.length; i++) {
                sendHeader(sender, bccList[i]);
                sendContent(sender, recipient, CCs, Bccs, subject, content);
            }
        }
        sendCommend("QUIT");
        readResponseLine();
    }

    public void sendHeader(String sender, String recipient) throws IOException {
        sendCommend("MAIL FROM:" + "<" + sender + ">");
        readResponseLine();
        sendCommend("RCPT TO:" + "<" + recipient + ">");
        readResponseLine();
    }

    public void sendContent(String sender, String recipient, String cc, String bcc, String subject, String content) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
        //get current date time with Date()
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        System.out.println(dateFormat.format(date));
        // Write mail
        sendCommend("DATA");
        readResponseLine();
        sendCommend("From: " + sender);
        sendCommend("To: " + recipient);
        sendCommend("cc: " + cc);
        sendCommend("Bcc: " + bcc);
        sendCommend("Subject: " + subject);
        sendCommend("Date: "+ currentDate);
        sendCommend(content);

//        sendCommend(".");
        sendCommend("\r\n.\r\n");
        readResponseLine();
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

    public static void main(String[] args) throws IOException {
        boolean ssl = true;
        SMTPDemo client;
        if( ssl) {
            client = new SMTPDemo(SMTPServerSSL, SMTPPort_GmailSSL);
        }else {
            client = new SMTPDemo(SMTPServerTCL, SMTPPort);
        }

        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (client.isConnected()) {
            // read response of hand shake for Gmail(SSL)
            if (client.SSL) {
                // Hand shake
//                client.sendCommend("EHLO " + SMTPServerSSL);
                client.sendCommend("HELO ");
//                for (int i = 0; i<8; i++) {
//                    client.readResponseLine();
//                }
                client.readResponseLine();
                client.readResponseLine();
                // Authentication
                client.sendCommend("AUTH LOGIN");
                client.readResponseLine();
                client.sendCommend(s);
                client.readResponseLine();
                client.sendCommend(p);
                client.readResponseLine();
                client.sendMail(senderSSL, receiverSSL, cc, bcc, subject, content);
            }
            else {
                // Hand shake
                client.sendCommend("HELO " + SMTPServerTCL);
                client.readResponseLine();
                client.readResponseLine();
                client.sendMail(senderTCL, receiverTCL, cc, bcc, subject, content);
            }
        }

        client.disconnect();




//            try {
//                // Hand shake
//                client.send("EHLO smtp.gmail.com");
//
//                // read response of hand shake
//                for (int i = 0; i<8; i++) {
//                    client.readResponseLine();
//                }
//
//                // Authentication
//                client.send("AUTH LOGIN");
//                client.readResponseLine();
//                client.send(s);
//                client.readResponseLine();
//                client.send(p);
//                client.readResponseLine();
//
//                client.send("MAIL FROM:" + "<" + sender + ">");
//                client.readResponseLine();
//                client.send("RCPT TO:" + "<" + receiver + ">");
//                client.readResponseLine();
//
//                // Write mail
//                client.send("DATA");
//                client.readResponseLine();
//                client.send("Subject: SMTP test");
//                client.send(content);
//                client.send("\r\n.\r\n");
//                client.readResponseLine();
//
//                client.send("QUIT");
//                client.readResponseLine();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                try {
//                    client.disconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
