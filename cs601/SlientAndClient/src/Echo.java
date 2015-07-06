/**
 * Created by treexy1230 on 2/17/15.
 */
import java.net.*;
import java.io.*;

public class Echo {
    public static void main(String[] args) throws IOException {
        int port = Integer.valueOf(args[0]);
        ServerSocket s = new ServerSocket(port);
        while ( true ) {
            System.out.println("waiting for connection...");
            Socket channel = s.accept();
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            PrintStream pout = new PrintStream(out);
            // Send "hello"
            pout.println("Hello!");
            DataInputStream din = new DataInputStream(in);
            String line = din.readLine();
            while ( line != null ) {
                System.out.println("client> " + line + "\n");
                pout.println(line);
                line = din.readLine();
            }
            din.close();
            channel.close();
        }
    }
}
