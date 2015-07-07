package cs601.webmail;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import cs601.webmail.managers.*;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopServer {
    private boolean debug = false;
    private Socket client;
    private BufferedReader sockin;
    private PrintWriter sockout;

    public PopServer(String pop, int port) throws IOException {
        connect(pop, port);
        sockin = new BufferedReader(new InputStreamReader(client.getInputStream()));
        sockout = new PrintWriter(client.getOutputStream(), true);
    }

    public void connect(String host, int port) throws IOException {
        if (port == 995) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            client = sslsocketfactory.createSocket(host, port);
        } else if (port == 110) {
            client = new Socket(host, port);
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

    //    public ArrayList<Email> fetchingMails(User u) throws IOException {
    public ArrayList fetchingMails(EmailAccount account) throws IOException {
        ArrayList<Email> mails = new ArrayList<>();
        String email = account.getEmail();
        String password = account.getPassword();
        if (isConnected()) {
            try {
                readResponseLine();
                sendCommand("USER " + "Recent:" + email);
                readResponseLine();
                sendCommand("PASS " + password);
                readResponseLine();

                mails = fetchingAllMails(account);
                sendCommand("QUIT");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }
        return  mails;
    }

    // Help function to parse string of addresses into "11@com, bb@com"
    private String getString(List<Address> list) {
        StringBuilder strs = new StringBuilder();

        for (Address a : list) {
            Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(a.toString());
            if (m.find()) {
                if (list.size() > 1 && !a.equals(list.get(list.size() - 1))) {
                    strs.append(m.group() + ", ");
                } else
                    strs.append(m.group());
            }
        }
        return strs.toString();
    }

    private String parseDate(String emailStr) throws ParseException {
        //parsed date: Sun Apr 19 12:04:55 PDT 2015 to "2015/04/19 12:04:55" in order to sort by date when displays
        SimpleDateFormat fromMail = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String reformattedStr = myFormat.format(fromMail.parse(emailStr));
        if (debug) System.out.println("Date: " + reformattedStr);
        return reformattedStr;
    }

    private ArrayList fetchingAllMails(EmailAccount account) throws Exception {
        MimeMessage message;
        DataSource dataSource;
        String fileName="";
        String decodedContent="";
        Attachment attach = null;
        ArrayList<Email> emailList = new ArrayList<>();
        // Get uidl of all Mails
        sendCommand("UIDL");
        readResponseLine();
        String uidl = sockin.readLine();
        HashMap<String, String> uidls = new HashMap<>();
        while (!uidl.equals(".")) {
            String[] UIDLs = uidl.split(" ");
            uidls.put(UIDLs[0], UIDLs[1]);
            uidl = sockin.readLine();
        }

        HashMap<String, String> needPullMails = new EmailModel().objects.checkMail(uidls);

        for (Map.Entry<String, String> entry : needPullMails.entrySet()) {
            ArrayList<Attachment> attachList = new ArrayList<>();
            String entireContent;
            String plainContent = "";
            String htmlContent = "";
            String contents = "";
            sendCommand("RETR " + entry.getKey());
            readResponseLine();

            if (debug) System.out.println("Mail " + entry.getKey() + " content: ");
            String emailStr = sockin.readLine();
            StringBuilder email = new StringBuilder();
            while (!emailStr.equals(".")) {
                email.append(emailStr + "\n");
                emailStr = sockin.readLine();
            }

            Session s = Session.getDefaultInstance(new Properties());
            InputStream is = new ByteArrayInputStream(email.toString().getBytes());
            message = new MimeMessage(s, is);
            MimeMessageParser mparser = new MimeMessageParser(message);
            String date = message.getSentDate().toString();
            String parsedDate = parseDate(date);
            mparser.parse();
            String sender = mparser.getFrom();
            String recipients = getString(mparser.getTo());
            String cc = getString(mparser.getCc());
            String bcc = getString(mparser.getBcc());
            String subject = mparser.getSubject();
            if (subject == null) {
                subject = "";
            }

            if (mparser.hasPlainContent()) {
                plainContent = mparser.getPlainContent();
            }
            if (mparser.hasHtmlContent()) {
                htmlContent = mparser.getHtmlContent();
            }
            if (mparser.hasAttachments()) {
                ArrayList<DataSource> attachments = (ArrayList) mparser.getAttachmentList();
                for (DataSource attch: attachments) {
                    fileName = attch.getName();
                    InputStream instr = attch.getInputStream();

                    Base64InputStream base64InputStream = new Base64InputStream(instr, true, -1, null);
                    byte[] retr = IOUtils.toByteArray(base64InputStream);
                    String base64Content = new String(retr);
                    attach = new Attachment(0, 0, fileName, base64Content);
                    attachList.add(attach);
                }
            }

            entireContent = plainContent + htmlContent + contents;
            if(debug) System.out.println(entireContent);

            // Distinguish inbox mail and outbox mail
            if (recipients.contains(account.getEmail())||cc.contains(account.getEmail())||bcc.contains(account.getEmail())) {
                emailList.add(new Email(0, account.getUserID(), sender, recipients, cc, bcc, subject,
                        entireContent, parsedDate, "in", entry.getValue(), "0", 0, attachList));
            }
            else if((account.getEmail()).equals(sender)){
                emailList.add(new Email(0, account.getUserID(), sender, recipients, cc, bcc, subject,
                        entireContent, parsedDate, "out", entry.getValue(), "0", 0, attachList));
            }
            else {
                emailList.add(new Email(0, account.getUserID(), sender, recipients, cc, bcc, subject,
                        entireContent, parsedDate, "in", entry.getValue(), "0", 0, attachList));
            }

            // store attachment info into db

        }

        return emailList;
    }



}
