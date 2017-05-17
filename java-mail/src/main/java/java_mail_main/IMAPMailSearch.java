package java_mail_main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailStore;

/**
     * Reference:
     * gimap javadoc https://javamail.java.net/nonav/docs/api/com/sun/mail/gimap/package-summary.html
     * gmail search syntax https://support.google.com/mail/answer/7190?hl=en
     * starter sample http://stackoverflow.com/questions/10291705/faster-reading-of-inbox-in-java
     */
    public class IMAPMailSearch {

        @SuppressWarnings("deprecation")
        public static void main(String[] args) throws Exception {

            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "gimap");


            try {
                
                
                // Search criteria for inbox
                Date current = new Date();
                Date pastDate = new Date(current.getYear(), current.getMonth(), current.getDate() - 1);
                SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, current);
                SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, pastDate);
                SearchTerm received = new ReceivedDateTerm(ComparisonTerm.EQ, current);

                
                Session session = Session.getDefaultInstance(props, null);
                GmailStore store = (GmailStore) session.getStore("gimap");
                store.connect("imap.gmail.com", "brittany.a.kelly@gmail.com", "Freddie10.14");
                GmailFolder inbox = (GmailFolder) store.getFolder("Inbox");
                GmailFolder receipts = (GmailFolder) store.getFolder("Receipts");
                inbox.open(Folder.READ_ONLY);
                receipts.open(Folder.READ_ONLY);
                Message[] foundMessages = inbox.search(received);
                

                ArrayList<Message> msgs = new ArrayList<Message>(foundMessages.length);
                for (int i = foundMessages.length - 1; i >= 0; i--) {
                   Message message = foundMessages[i];
                   if (message.getSubject().toLowerCase().contains("receipt") ||
                           getTextFromMessage(message).toLowerCase().contains("receipt") ||
                           getTextFromMessage(message).toLowerCase().contains("$")){
                       msgs.add(foundMessages[i]);
                       
                   }
                   

                }
                writeMessagesToFile(msgs);
                
                receipts.close(false);
                inbox.close(false);
                store.close();

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (MessagingException e) {
                e.printStackTrace();
                System.exit(2);
            }
            System.out.println("Done");
            
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
        
        @SuppressWarnings("deprecation")
        public static void writeMessagesToFile(ArrayList<Message> msgs) {
            FileWriter fw = null;
            BufferedWriter bw = null;
            String filename = "";
            try {
                for (int i = 0; i < msgs.size() - 1; i++) {

                    filename = Integer.toString(msgs.get(i).getReceivedDate().getMonth()) +
                            Integer.toString(msgs.get(i).getReceivedDate().getDay()) ;
                    File file = new File("C:\\Users\\Brittany\\Documents\\Receipts\\Receipts_" + i + ".txt");
                    fw = new FileWriter(file, false);
                    bw = new BufferedWriter(fw);
                    
                    bw.write("---------------------------------");
                    bw.newLine();
                    bw.write("MESSAGE " + i + 1);
                    bw.newLine();
                    bw.write("DATE: " + msgs.get(i).getReceivedDate().toString());
                    bw.write("SUBJECT: " + msgs.get(i).getSubject());
                    bw.newLine();
                    bw.write("FROM: " + msgs.get(i).getFrom());
                    bw.newLine();
                    bw.write("BODY:");
                    bw.newLine();
                    bw.write(getTextFromMessage(msgs.get(i)));
                    bw.newLine();
                    bw.flush();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

