
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.codec.binary.Base64;


/**
 * Created by treexy1230 on 4/25/15.
 */
public class TotalTemp
{
    private static DataOutputStream dos;

    public static void main(String[] args) throws Exception
    {
        int delay = 1000;
//        String username = Base64.encode("xyz@gmail.com");
//        String password = Base64.encode("XYZ");

        String s = new String(Base64.encodeBase64("yxu66@dons.usfca.edu".getBytes()));
         String p = new String(Base64.encodeBase64("onlineyxu66".getBytes()));

        SSLSocket sock = (SSLSocket)((SSLSocketFactory)SSLSocketFactory.getDefault()).createSocket("smtp.gmail.com", 465);
//          Socket sock = new Socket("smtp.gmail.com", 587);
        final BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        (new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    String line;
                    while((line = br.readLine()) != null)
                        System.out.println("SERVER: "+line);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        })).start();
        dos = new DataOutputStream(sock.getOutputStream());

        send("EHLO smtp.gmail.com\r\n");
        Thread.sleep(delay);
        send("AUTH LOGIN\r\n");
        Thread.sleep(delay);
        send(s);
        Thread.sleep(delay);
        send(p);
        Thread.sleep(delay);
        send("MAIL FROM:<yxu66@dons.usfca.edu>\r\n");
        send("\r\n");
        Thread.sleep(delay);
        send("RCPT TO:<yxu66mail@gmail.com>\r\n");
        Thread.sleep(delay);
        send("DATA\r\n");
        Thread.sleep(delay);
        send("Subject: Email test\r\n");
        Thread.sleep(delay);
        send("Test 1 2 3");
        Thread.sleep(delay);
        send("\r\n.\r\n");
        Thread.sleep(delay);
        send("QUIT\r\n");
    }

    private static void send(String s) throws Exception
    {
        dos.writeBytes(s);
        System.out.println("CLIENT: "+s);
    }
}
