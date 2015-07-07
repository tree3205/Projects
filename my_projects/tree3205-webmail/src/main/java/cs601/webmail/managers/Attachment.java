package cs601.webmail.managers;

public class Attachment {
    int attachID;
    int mailID;
    String fileName;
    String fileContent;
    public Attachment(int attachID, int mailID, String fileName, String fileContent) {
        this.attachID = attachID;
        this.mailID = mailID;
        this.fileName =fileName;
        this.fileContent = fileContent;
    }

    public int getAttachID(){return attachID;}
    public int getMailID(){return mailID;}
    public void setMailID(int mailID){this.mailID = mailID;}
    public void setAttachID(int attachID){ this.attachID = attachID;}
    public String getFileName(){return fileName;}
    public void setFileName(String fileName){this.fileName = fileName;}
    public String getFileContent(){return fileContent;}
    public void setFileContent(String fileContent){this.fileContent = fileContent;}
}
