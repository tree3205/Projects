package cs601.proxy;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {

    public static final int HTTP = 80;
    public static final int DATA_SIZE = 8192;
    protected boolean debug = false;
    private String badRequest = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
                                "<html>\n" +
                                "\n" +
                                "<head>\n" +
                                "   <title>400 Bad Request</title>\n" +
                                "</head>\n" +
                                "\n" +
                                "<body>\n" +
                                "   <h1>400 Bad Request</h1>\n" +
                                "   <p>Server could not interpret or understand the request.</p>\n" +
                                "</body>\n" +
                                "\n" +
                                "</html>";
    private Socket clientSocket;
    private Socket channel;
    private String host;
    private DataInputStream fromBrowser;
    private BufferedOutputStream toBrowser;
    private DataInputStream fromRemote;
    private BufferedOutputStream toRemote;
    private Map<String, String> headerInfo = new LinkedHashMap<>();
    private Map<String, String> requestInfo = new HashMap<>();


    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        fromBrowser = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        toBrowser = new BufferedOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            readRequest();
            getHeaders();
            makeUpstreamRequest();
            boolean succ = openUpstreamSocket(host);
            if (succ){
                process();
                int contentLen = writeHeaders();
                forwardRemoteDataToBrowser(contentLen);
                toRemote.close();
                fromRemote.close();
                channel.close();
            }
            fromBrowser.close();
            toBrowser.close();
            clientSocket.close();
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
                System.out.println("Error happened using proxy server");
            }
        }
    }

    /**  1. get first line from browser
     *   2. ignore and close the connection if there is no command
     *   3. split into the command, the URI, and the HTTP version;
     *      e.g. for this apart: "GET http://xyz.com/foo HTTP/1.0"
     *    =========> done by readRequest method()*/
    public void readRequest() throws IOException{
        String line = fromBrowser.readLine();
        String[] data;
        if (line != null && line.length() > 0){
            data = line.split(" ");
            requestInfo.put("command", data[0]);
            requestInfo.put("URI", data[1]);
            requestInfo.put("HTTP version", data[2]);
        }
        else {
            clientSocket.close();
        }
    }

    public byte[] readData(DataInputStream in, int size) throws IOException{
        byte[] buf = new byte[size];
        in.readFully(buf, 0, size);
        return buf;
    }

    /**   4. read in headers until you see a blank line; force the header names to lowercase
     *    and put the name-value pairs into a map
     *    =========> done by getHeaders method()*/
    public void getHeaders() throws IOException {
        String line = fromBrowser.readLine();
        if (line != null && line != "") {
            line = line.toLowerCase();
        }
        else {
            line = fromBrowser.readLine();
        }
        String[] tmp = line.split(": ");
        host = tmp[1];
        while (line != null && line.length() > 0) {
            // Store name, value pair in map
            String[] l = line.split(": ");
            headerInfo.put(l[0], l[1]);
            line = fromBrowser.readLine().toLowerCase();
        }
    }

    /** 5. strip out user-agent, referer, proxy-connection headers
     *  6. strip out connection header if its value is keep-alive
     *  7. parse the URI to strip out the host and get the "file" name like /foo
     *    =========> done by makeUpstreamRequest method()*/
    public void makeUpstreamRequest() throws MalformedURLException {
        headerInfo.remove("user-agent");
        headerInfo.remove("referer");
        headerInfo.remove("proxy-connection");

        if (headerInfo.containsKey("connection") &&
            headerInfo.get("connection").equals("keep-alive")){
            headerInfo.remove("connection");
        }

        // parse URI, update requestInfo
        String URI = requestInfo.get("URI");

        // Group: ((A)(B(C)))
        // 1. ((A)(B(C)))
        // 2. (A)
        // 3. (B(C))
        // 4. (C)
        String p = "((http[s]?://)([^/]+(/(.*))))";

        // Regular expression to find file, maybe a problem for complex website
        String file = null;
        Pattern r = Pattern.compile(p);
        Matcher m = r.matcher(URI);
        if (m.find()) {
            file = m.group(4);
        } else {
            System.out.println("NO MATCH FILE");
        }
        requestInfo.put("URI", file);

        // check http version, and update requestInfo
        if (requestInfo.get("HTTP version").equals("HTTP/1.1")) {
            requestInfo.put("HTTP version", "HTTP/1.0");
        }
    }

    /**   8. open a socket at port 80 at the remote host
     *    =========> done by openUpstreamSocket method()*/
    public boolean openUpstreamSocket(String host) throws IOException{
        boolean success = false;
        try {
            channel = new Socket(host, HTTP);
            fromRemote = new DataInputStream(new BufferedInputStream(channel.getInputStream()));
            toRemote = new BufferedOutputStream(channel.getOutputStream());
            success = true;
        } catch (UnknownHostException e) {
            toBrowser.write("HTTP/1.1 400 Bad Request\r\n".getBytes());
            toBrowser.write("\r\n".getBytes());
            toBrowser.write(badRequest.getBytes());
            toBrowser.flush();
        }
        return success;
    }

    /**   9. send it the same HTTP command that you got from the browser except make the version 1.0
     *    not 1.1 so we don't have to worry about chucking and use the file name not the entire URI
     *    10. then send the upstream host all of the headers we got from the browser minus the ones we deleted
     *    11. if POST, get the content-length header from the browser and copy the data following
     *     the browser headers to the socket to the upstream host
     *     =========> done by process method()*/
    public void process() throws IOException {
        // Parse request and send it to remote.
        String req = requestInfo.get("command") + " " +
                     requestInfo.get("URI") + " " +
                     requestInfo.get("HTTP version") + "\r\n";

        // Parse header
        String headers = "" ;
        for (String k: headerInfo.keySet()) {
            String line = k + ": " + headerInfo.get(k) + "\r\n";
            headers += line;
        }
        toRemote.write(req.getBytes());
        toRemote.write(headers.getBytes());
        toRemote.write("\r\n".getBytes());
        toRemote.flush();

        int contentLen;
        if (requestInfo.get("command").equals("POST")) {
            contentLen = Integer.valueOf(headerInfo.get("content-length"));
            byte[] data = readData(fromBrowser, contentLen);
            toRemote.write(data);
            toRemote.write("\r\n".getBytes());
        }

        if (debug) {
            System.out.println("Browser req: \n" + req);
            System.out.println("Browser headers: \n" + headers);
        }
    }

    /** 12. read the response line from the remote host like HTTP/1.0 200 OK
     *  13. send it back to the browser
     *  14. strip out connection header from the remote host response headers if its value is keep-alive
     *  15. send upstream host response headers back to the browser*/
    public int writeHeaders() throws IOException {
        int contentLen = -1;
        String header = "";
        String response;

        String line = fromRemote.readLine();
        while (line == "" || line == null) {
            line = fromRemote.readLine();
        }
        response = line;
        while (line != null && line.length() > 0) {
            if (line.toLowerCase().startsWith("content-length")){
                int colon = line.indexOf(":");
                String opnd = line.substring(colon + 1);
                contentLen = Integer.valueOf(opnd.trim());
            }
            if (!response.equals("HTTP/1.1 302 Moved Temporarily")) {
                if (line.toLowerCase().contains("connection") && line.toLowerCase().contains("keep_alive")) { continue;}
            }
            header += line + "\r\n";
            line = fromRemote.readLine();
        }
        // Send response and headers back to browser
        toBrowser.write(header.getBytes());
        toBrowser.write("\r\n".getBytes());
        toBrowser.flush();
        if (debug) System.out.println("Remote feedback: \n" + header);
        return contentLen;
    }

    /**  16. read upstream data and pass it back to the browser
     *   17. flush and close the Socket
     *   =========> done by forwardRemoteDataToBrowser method()
     */
    public void forwardRemoteDataToBrowser(int contentLen) throws IOException {
        int contentLeft;
        int sizeToRead = DATA_SIZE;
        if (contentLen != -1) {
            contentLeft = contentLen;
            while (contentLeft > 0) {
                if (contentLeft < DATA_SIZE) {
                    sizeToRead = contentLeft;
                }
                byte[] data = readData(fromRemote, sizeToRead);
                toBrowser.write(data);
                toBrowser.flush();
                contentLeft -= sizeToRead;
            }
        }
        else {
            int c;
            while( (c = fromRemote.read()) != -1) {
                toBrowser.write(c);
            }
        }
        toBrowser.write("\r\n".getBytes());
        toBrowser.flush();
    }
}
