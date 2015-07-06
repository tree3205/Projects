import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by treexy1230 on 4/21/15.
 */
class POP3Client {
    private Socket clientSocket;
    private boolean debug = false;
    private BufferedReader in;
    private BufferedWriter out;
    private static final int PORT = 110;

    public boolean isDebug() {
        return debug;
    }

    public void connect(String host, int port) throws IOException {
        debug = true;
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(host, port));
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        if (debug)
            System.out.println("Connected to the host");
        readResponseLine();
    }

    public void connect(String host) throws IOException {
        connect(host, PORT);
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    public void disconnect() throws IOException {
        if (!isConnected())
            throw new IllegalStateException("Not connected to a host");
        clientSocket.close();
        in = null;
        out = null;
        if (debug)
            System.out.println("Disconnected from the host");
    }

    protected String readResponseLine() throws IOException {
        String response = in.readLine();
        if (debug) {
            System.out.println("DEBUG [in] : " + response);
        }
        if (response.startsWith("-ERR"))
            throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
        return response;
    }

    protected String sendCommand(String command) throws IOException {
        if (debug) {
            System.out.println("DEBUG [out]: " + command);
        }
        out.write(command + "\n");
        out.flush();
        return readResponseLine();
    }

    public void login(String username, String password) throws IOException {
        sendCommand("USER " + username);
        sendCommand("PASS " + password);
    }

    public void logout() throws IOException {
        sendCommand("QUIT");
    }

    public int getNumberOfNewMessages() throws IOException {
        String response = sendCommand("STAT");
        String[] values = response.split(" ");
        return Integer.parseInt(values[1]); //value[0] - busena, value[1] - pranesimu skaicius value[2] - pranesimu uzimama vieta
    }


//    protected Message getMessage(int messageNumber) throws IOException {
//        String response = sendCommand("RETR " + messageNumber);
//        HashMap<String, LinkedList<String>> headers = new HashMap<String, LinkedList<String>>();
//        String headerName = null;
//        // process headers
//        while ((response = readResponseLine()).length() != 0) {
//            if (response.startsWith("\t"))
//                continue; //no process of multiline headers
//
//            int colonPosition = response.indexOf(":");
//            headerName = response.substring(0, colonPosition);
//            String headerValue;
//            if (headerName.length() > colonPosition)
//                headerValue = response.substring(colonPosition + 2);
//            else
//                headerValue = "";
//
//            LinkedList<String> headerValues = headers.get(headerName);
//            if (headerValues == null) {
//                headerValues = new LinkedList<String>();
//                headers.put(headerName, headerValues);
//            }
//            headerValues.add(headerValue);
//        }
//        // process body
//        StringBuilder bodyBuilder = new StringBuilder();
//        while (!(response = readResponseLine()).equals(".")) {
//            bodyBuilder.append(response + "\n");
//        }
//        return new Message(headers, bodyBuilder.toString());
//    }
//
//    public LinkedList<Message> getMessages() throws IOException {
//        int numOfMessages = getNumberOfNewMessages();
//        LinkedList<Message> messageList = new LinkedList<Message>();
//        for (int i = 1; i <= numOfMessages; i++) {
//            messageList.add(getMessage(i));
//        }
//        return messageList;
//    }
//

}
