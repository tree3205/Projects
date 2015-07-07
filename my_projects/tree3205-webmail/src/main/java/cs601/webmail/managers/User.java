package cs601.webmail.managers;


public  class User {
    int ID;
    String userName;


    public User(int ID, String userName) {
        this.ID = ID;
        this.userName = userName;
    }

    public String getName() { return userName; }
    public int getUserID() { return ID; }


    public String toString() { return ID +": " + userName; }
}
