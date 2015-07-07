package cs601.webmail.managers;

import java.util.ArrayList;

public  class Email {
    int mailID;
    int userID;
    String sender;
    String receiver;
    String cc;
    String Bcc;
    String subject;
    String content;
    String date;
    String mailType;
    String uidl;
    String ifRead;  // unread = 0; read = 1
    int folderID;  // unread = 0; read = 1
    ArrayList<Attachment> attach;

    public Email(int mailID, int userID, String sender, String receiver, String cc,
                 String Bcc, String subject, String content, String date, String mailType,
                 String uidl, String ifRead, int folderID, ArrayList<Attachment> attach) {
        this.mailID = mailID;
        this.userID = userID;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.cc = cc;
        this.Bcc = Bcc;
        this.content = content;
        this.date = date;
        this.mailType = mailType;
        this.uidl = uidl;
        this.ifRead = ifRead;
        this.folderID = folderID;
        this.attach = attach;
    }

    public int getMailID() { return mailID; }
    public void setMailID(int mailID) { this.mailID = mailID; }
    public int getUserID() { return userID; }
    public String getSender() { return sender; }
    public String getReceiver() {return  receiver;}
    public String getCC() {return cc;}
    public String getBcc() {return Bcc;}
    public String getSubject() {return  subject;}
    public String getContent() {return  content;}
    public String getDate() {return date;}
    public String getType() {return mailType;}
    public void setType(String mailType) {this.mailType = mailType;}
    public String getUIDL() {return uidl;}
    public String getIfRead() {return ifRead;}
    public int getFolderID() {return folderID;}
    public ArrayList<Attachment> getAttachment() {return attach;}
    public void setAttachment(ArrayList<Attachment> attachList) {this.attach = attachList;}
    public void setIfRead(String ifRead) {this.ifRead = ifRead;}
    public void setDate(String time) {date = time;}
    public void setContent(String content) {this.content = content;}

    public String toString() { return String.format("mailID: %d\nUserID: %d\nSender: %s\nReceiver: %s\n" +
                                "cc: %s\nBcc: %s\nSubject: %s\nContent: %s\nDate: %s\nMailType: %s\nUIDL: %s\n" +
                    "IfRead: %s\nfolderID: %s",
            mailID, userID, sender, receiver, cc, Bcc, subject, content, date, mailType, uidl, ifRead, folderID); }
}
