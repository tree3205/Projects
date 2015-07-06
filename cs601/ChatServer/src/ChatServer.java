/**
 * Created by treexy1230 on 2/19/15.
 */
import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    /** Map usernames to output stream for broadcasting */
    protected Map<String,PrintStream> users = new HashMap<String, PrintStream>();

    /** Which port should the server listen at? */
    protected int port;
    public ServerSocket ss;

    public ChatServer(int port) {
        this.port = port;
    }

    public void startup() throws IOException {
        System.out.println("ChatServer listening at port "+ port);
        Socket socket = null;
        // forever do:
        // Wait for connection from a client
        // Create and launch a new client handler
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            Socket channel = ss.accept();
            ChatClientHandler clientHandler = new ChatClientHandler(this,channel);
            Thread t = new Thread(clientHandler);
            t.start();
        }
    }

    /** Track that this user name has this output stream */
    public void registerClient(String user, PrintStream out) {
        // broadcast to everyone that user has "connected"
        // print "welcome user" to the client
        // print to standard out: "user connected"
        // add the user,out pair to the users map
        broadcast(user, "connected");
        out.println("welcome " + user);
        System.out.println(user + " connected");
        users.put(user, out);
    }

    /** Send a string to everybody but user */
    public void broadcast(String user, String line) {
        // For each user u in users
        // print to u's output stream: "user: line" if u not user
        for (String u: users.keySet()) {
            if (!u.equals(user)) {
                PrintStream p = users.get(u);
                p.println(user + ": " + line);
            }
        }
    }

    public void disconnect(String user) {
        // remove user from users table
        // broadcast "disconnected" message
        users.remove(user);
        broadcast(user, "disconnected");
    }

    public static void main(String[] args) throws IOException {
        ChatServer s = new ChatServer(8000);
        s.startup();
    }
}