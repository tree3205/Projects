/**
 * Created by treexy1230 on 2/17/15.
 */
import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 8080);
        OutputStream out = s.getOutputStream();
        PrintStream pout = new PrintStream(out);
        InputStream input = s.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String str = in.readLine();
        // Read line from stdin.
        System.out.println("handshake> " + str);
        String stdin;
        while (str != null) {
            // Read another line from stdin.
            stdin = br.readLine();
            System.out.println("stdin:" + stdin);
            // Send line to server.
            pout.println(stdin);
            // Read line from server, print it.
            str = in.readLine();
            System.out.println("server> " + str);
        }
        pout.close();
        s.close();
    }
}