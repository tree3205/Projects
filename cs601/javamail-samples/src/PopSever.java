import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.language.SoundexTest;
import org.apache.commons.mail.util.MimeMessageParser;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.python.core.*;


/**
 * Created by treexy1230 on 4/21/15.
 */
public class PopSever {
//    private static String sender = "yxu66@dons.usfca.edu";
    private static String sender = "yxu66mail@gmail.com";
    private static String password = "mailyxu66";
    private static String receiver = "yxu66mail@gmail.com";
    private static String cc = "treexy1230@gmail.com";
    private static String bcc = "treexy1230@gmail.com";
    private static String POP3Server = "pop.gmail.com";

    private static String s = new String(Base64.encodeBase64(sender.getBytes()));
    private static String p = new String(Base64.encodeBase64(password.getBytes()));
    private static BufferedReader sockin;
    private static PrintWriter sockout;
    private static boolean debug = true;
    private static final int POP3Port_Gmail = 995;
    private static final int POP3Port = 110;

    private static Socket client;
    public static String content = "Test!";
    public boolean SSL = true;
    public static PopSever popSever;

    public void connect(String host, int port) throws IOException {
        if (this.SSL) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            client = sslsocketfactory.createSocket(host, port);
        } else {
            client = new Socket(host, port);
        }
        sockin = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sockout = new PrintWriter(client.getOutputStream(), true);

        if (debug) System.out.println("Connected to the host");
    }

    public boolean isConnected() {
        return client != null;
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

    private void send(String str) {
        sockout.println(str);
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
        popSever = new PopSever();
        popSever.connect(POP3Server, POP3Port_Gmail);

        if (popSever.isConnected()) {
            try {
                popSever.readResponseLine();
                popSever.send("USER " + "Recent:"+ sender);
                popSever.readResponseLine();
                popSever.send("PASS " + password);


                popSever.readResponseLine();
                popSever.send("STAT");
                String temp[] = sockin.readLine().split(" ");
                int count = Integer.parseInt(temp[1]);
                if (debug) System.out.println("Num of messages: " + count);

                popSever.fetchingMessages(count);
                popSever.send("QUIT");

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                popSever.disconnect();
            }
        }
    }

    private void fetchingMessages(int count) throws Exception {
        MimeMessage message;
//        // Get uidl of all Mails
//        send("UIDL");
//        readResponseLine();
//        String uidl = sockin.readLine();
//        HashMap<String, String> uidls = new HashMap<>();
//        while (!uidl.equals(".")) {
//            String[] aUIDL = uidl.split(" ");
//            System.out.println("UIDL: "+ aUIDL[0]+"-"+ aUIDL[1]);
//            uidls.put(aUIDL[0], aUIDL[1]);
//            uidl = sockin.readLine();
//        }


//        for (int i = 1; i < count + 1; i++) {
            popSever.send("RETR 66");
            popSever.readResponseLine();

            if (debug) System.out.println("Mail " + "66" + " content: ");
            String emailStr = sockin.readLine();
            StringBuilder email = new StringBuilder();
            // Need to get Date

            String date = "";
            while (!emailStr.equals(".")) {
//                if (emailStr.startsWith("Date: ")) {
//                    date = parseDate(emailStr);
//                }
                email.append(emailStr + "\r\n");
                emailStr = sockin.readLine();
            }
            System.out.println("email 66");
            System.out.println(email.toString());
            Session s = Session.getDefaultInstance(new Properties());
            InputStream is = new ByteArrayInputStream(email.toString().getBytes());
            message = new MimeMessage(s, is);
            MimeMessageParser mparser = new MimeMessageParser(message);
            String parsedDate1 = message.getSentDate().toString();
            System.out.println("parsedDate1: "+ parsedDate1);
            System.out.println("after parse:" + parseDate(parsedDate1));
            System.out.println("====================");


            String sender = mparser.getFrom();
            List<Address> recipients =  mparser.getTo();
            List<Address> cc = mparser.getCc();
            List<Address> bcc =  mparser.getBcc();
            String subject = mparser.getSubject();
            if(debug) {
//                System.out.println("sender: "+sender);
//                System.out.println("recipient: "+recipients);
//                System.out.println("cc: "+cc);
//                System.out.println("bcc: "+bcc);
//                System.out.println("subject: "+subject);
                System.out.println("Date: " + date);
            }

            mparser.parse();

//            StringBuilder strb = new StringBuilder();
//            String content = sockin.readLine();
//            while(!content.toLowerCase().equals("\r\n.\r\n")){
//                strb.append(content);
//            }
//            System.out.println(strb.toString());
//            System.out.println("--------------------------");

//            if (mparser.hasPlainContent()) {
//                String plainContent = mparser.getPlainContent();
//                if(debug) {
//                    System.out.println("This is plain content");
//                    System.out.println(plainContent);
//                    System.out.println("-----------------------------");
//                }
//            }
//            else if (mparser.hasHtmlContent()) {
//                String htmlContent = mparser.getHtmlContent();
//                if(debug) {
//                    System.out.println("This is html content");
//                    System.out.println(htmlContent);
//                    System.out.println("-----------------------------");
//                }
//            }
//            else if (mparser.isMultipart()) {
//                if(debug) {
//                    System.out.println("This is multi part content");
//                    System.out.println("-----------------------------");
//                }
//            }
//            else if (mparser.hasAttachments()) {
//                ArrayList attachments = (ArrayList) mparser.getAttachmentList();
//                if(debug) {
//                    System.out.println("These are attachments");
//                    for ( Object attch: attachments) {
//                        System.out.println(attch.toString());
//                    }
//                    System.out.println("-----------------------------");
//                }
//            }






//            Address sender = message.getSender();
//            Address[] receipent = message.getRecipients(Message.RecipientType.TO);
//            Address[] cc = message.getRecipients(Message.RecipientType.CC);
//            Address[] bcc = message.getRecipients(Message.RecipientType.BCC);
//            String subject = message.getSubject();
//            Object content = message.getContent();
//            // check if the content is text
//            if (content instanceof String) {
//                if (debug) {
//                    System.out.println("This is a string");
//                    System.out.println("---------------------------------");
//                    System.out.println((String) content);
//                }
//            }
//            //check if the content has attachment
//            else if (content instanceof MimeMultipart) {
//                System.out.println("This is a Multipart");
//                Multipart mp = (Multipart) content;
//                int num = mp.getCount();
//                for (i = 0; i < num; i++) {
//                    String c = sockin.readLine();
//                    System.out.println(c);
//                }
//                System.out.println("---------------------------------");
//
//            } else if (content instanceof InputStream) {
//                System.out.println("This is just an input stream");
//                System.out.println("---------------------------------");
//                InputStream in = (InputStream) content;
//                int c;
//                while ((c = in.read()) != -1)
//                    System.out.write(c);
//            }
            // Create new object of Email in web mail and return it


//        }
    }

    private String parseDate(String emailStr) {
        //parsedDate: Sun Apr 19 12:04:55 PDT 2015
        //Date: Sun, 19 Apr 2015 12:04:55 -0700 (PDT)
//        String tmp = emailStr.substring(6);
//        System.out.println(emailStr);
        SimpleDateFormat fromMail1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
//        SimpleDateFormat fromMail2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss a");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String reformattedStr = null;
        try {
            reformattedStr = myFormat.format(fromMail1.parse(emailStr));
        } catch (ParseException e) {
//            try {
//                reformattedStr = myFormat.format(fromMail2.parse(tmp));
//            } catch (ParseException e1) {
//                e1.printStackTrace();
//            }
            e.printStackTrace();
        }
        System.out.println(reformattedStr);
        return reformattedStr;
    }
}


