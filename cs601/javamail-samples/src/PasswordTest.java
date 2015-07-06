import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Address;
import java.net.URL;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by treexy1230 on 4/25/15.
 */
public class PasswordTest {


    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue =
            new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

    public String encrypt(String valueToEnc) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encValue);
        return encryptedValue;
    }

    public String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

    public String getString(List<String> list) {
        StringBuilder strs = new StringBuilder();
        for (String a: list) {
            Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(a.toString());
            if (m.find()) {
                if(list.size() > 1 && !a.equals(list.get(list.size()-1))) {
                    strs.append(m.group() + ", ");
                }
                else
                    strs.append(m.group());
            }
        }

        return strs.toString();
    }


    private String parseDate(String emailStr) throws ParseException {
        //Date: Sun, 19 Apr 2015 12:04:55 -0700 (PDT)
        String tmp = emailStr.substring(6);
        System.out.println(tmp);
        SimpleDateFormat fromMail = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String reformattedStr = myFormat.format(fromMail.parse(tmp));
        System.out.println(reformattedStr);
        return reformattedStr;
    }


    public static void main(String[] args) throws Exception {
        PasswordTest t =  new PasswordTest();
//        String pwd ="password";
//        String encryptedPWD =  t.encrypt(pwd);
//        System.out.println(encryptedPWD);
//
//        String decryptedPWD = t.decrypt(encryptedPWD);
//        System.out.println(decryptedPWD);
//
//
//        String s = "yxu66mail <yxu66mail@gmail.com>, tree <treexy1230@gmail.com> ";
//        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(s);
//        while (m.find()) {
//            System.out.println(m.group());
//        }
//
//        String[] addresses = s.split(", ");
//        ArrayList<String> list = new ArrayList<>();
//        for (String ss: addresses) {
//            list.add(ss);
//        }
//        System.out.println(list);
//        String result = t.getString(list);
//        System.out.println(result);


//        t.parseDate("Date: Sun, 19 Apr 2015 12:04:55 -0700 (PDT)");
        String s = "http://localhost:8080/mail?mailID=1";
        URL aURL = new URL(s);
        String q = aURL.getQuery();
        System.out.println(q);
        String[] l = q.split("=");
        System.out.println(l[1]);
        String query = String.format("SELECT * FROM  Mails WHERE userID = %s AND type = "+"in"+" LIMIT %d, %d;",
                "1", 0, 12);
        System.out.println(query
        );
    }
}



