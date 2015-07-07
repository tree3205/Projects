package cs601.webmail.managers;

//Email(newID, userID, sender, receiver, cc, bcc, subject, sendContent, Status, date)
public  class EmailAccount {
    int accountID;
    int userID;
    String email;
    String password;
    String smtp;
    String pop;
    int smtpport;
    int popport;


    public EmailAccount(int accountID, int userID, String email, String password, String smtp,
                        String pop, int smtpport, int popport) {
        this.accountID = accountID;
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.smtp = smtp;
        this.pop = pop;
        this.smtpport = smtpport;
        this.popport = popport;
    }

    public int getAccountID () { return accountID; }
    public int getUserID() { return userID; }
    public String getEmail() {return  email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() {return  password;}
    public void setPassword(String password) {this.password = password;}
    public String getSMTP() {return  smtp;}
    public void setSMTP(String smtp) {this.smtp = smtp;}
    public String getPOP() {return  pop;}
    public void setPOP(String pop) {this.pop = pop;}
    public int getSMTPPort() {return smtpport;}
    public void setSMTPPort(int smtpport) {this.smtpport = smtpport;}
    public int getPOPPort() {return popport;}
    public void setPOPPort(int popport) {this.popport = popport;}

    public String toString() { return String.format("accountID: %d\nUserID: %d\nemail: %s\nsmtp: %s\n" +
                                "pop: %s\nsmtp_port: %s\npop_port: %s",
            accountID, userID, email, smtp, pop, smtpport, popport); }
}
