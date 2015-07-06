/**
 * Created by treexy1230 on 2/19/15.
 */
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ChatClientHandler implements Runnable {
    protected DataInputStream  in;
    protected PrintStream      out;
    protected Socket socket;
    protected ChatServer server;
    protected String user;

    public ChatClientHandler(ChatServer server, Socket socket)
            throws IOException
    {
        this.server = server;
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new PrintStream(socket.getOutputStream());

    }

    public void run() {
        try {
            // read the first line of input and set the user name
            // register this user,out pair with the server
            // read a line
            // while line is not null:
            // broadcast line
            // read another line from the client
            // disconnect user via server
            String usr = in.readLine();
            server.registerClient(usr, out);
            String line = in.readLine();
            while(line != null ) {
                server.broadcast(usr, line);
                line = in.readLine();
            }
            server.disconnect(usr);
            out.close();
            in.close();
            socket.close();
        }
        catch (IOException e) { // EOF
            server.disconnect(user);
        }
    }
}
