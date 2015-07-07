package cs601.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


/** The ProxyServery usable by common web browsers to access remote hosts anonymously.
 *  And it only respond to GET and POST requests. It will launches ClientHandler objects,
 *  one per socket connection.
 *  Basic algorithm:
 *  Open a server socket
 *      loop forever:
 *      wait for a socket connection
 *      create a client handler, passing the Socket object
 *      create and launch a Java thread attached to that client handler
 */
public class ProxyServer {

	public static final boolean SingleThreaded = false;
    protected int port;
    protected ServerSocket ss;

    /** Have proxy listen at port specified from the command line as the first argument,
     * defaulting to port 8080 if none is specified. */
    public ProxyServer(int port) {
        this.port = port;
    }

    /** Start the proxy server */
    public void startup() throws IOException {
        ServerSocket ss = new ServerSocket(port, 10);
        while(true) {
            Socket channel = ss.accept();
            ClientHandler clientHandler = new ClientHandler(channel);
            if (SingleThreaded) {
                clientHandler.run();
            }
            else {
                Thread t = new Thread(clientHandler);
                t.start();
            }
        }
    }

	public static void main(String[] args) throws IOException {
        int port;
        if (args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        else {
            port = 8080;
        }
        ProxyServer p = new ProxyServer(port);
        p.startup();
    }
}
