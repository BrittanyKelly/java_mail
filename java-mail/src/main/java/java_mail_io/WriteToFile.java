package java_mail_io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.MimeMultipart;

/**
 * This class facilitates the output of Message objects into individual files.
 * 
 * @author Brittany Kelly
 *
 */
public class WriteToFile {
    
    public WriteToFile() {
        
    } 
    
    @SuppressWarnings("deprecation")
    public void writeMessagesToFile(Message[] msgs) {
        System.out.println("2" );
        FileWriter fw = null;
        BufferedWriter bw = null;
        String filename = "";
        try {
            System.out.println("3" );
            for (int i = 0; i <= msgs.length - 1; i++) {
                System.out.println("4" );
                filename = Integer.toString(msgs[i].getReceivedDate().getMonth()) +
                        Integer.toString(msgs[i].getReceivedDate().getDay()) ;
                File file = new File("C:\\Users\\Brittany\\Documents\\Receipts\\Receipts_" + filename + ".txt");
                fw = new FileWriter(file, false);
                bw = new BufferedWriter(fw);
                
                bw.write("---------------------------------");
                bw.newLine();
                bw.write("MESSAGE " + i + 1);
                bw.newLine();
                bw.write("DATE: " + msgs[i].getReceivedDate().toString());
                bw.write("SUBJECT: " + msgs[i].getSubject());
                bw.newLine();
                bw.write("FROM: " + msgs[i].getFrom());
                bw.newLine();
                bw.write("BODY:");
                bw.newLine();
                bw.write(getTextFromMessage(msgs[i]));
                bw.newLine();
                bw.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getTextFromMessage(Message message) throws Exception {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    public static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws Exception{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

}
 