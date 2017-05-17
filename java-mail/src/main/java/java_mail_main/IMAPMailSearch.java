package java_mail_main;

import java.util.Date;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailStore;
import java_mail_io.WriteToFile;

/**
     * Reference:
     * gimap javadoc https://javamail.java.net/nonav/docs/api/com/sun/mail/gimap/package-summary.html
     * gmail search syntax https://support.google.com/mail/answer/7190?hl=en
     * starter sample http://stackoverflow.com/questions/10291705/faster-reading-of-inbox-in-java
     */
    public class IMAPMailSearch {

        public static void main(String[] args) throws Exception {

            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "gimap");

            try {
               
                Session session = Session.getDefaultInstance(props, null);
                GmailStore store = (GmailStore) session.getStore("gimap");
                store.connect("imap.gmail.com", "brittany.a.kelly@gmail.com", "Freddie10.14");
                GmailFolder inbox = (GmailFolder) store.getFolder("Inbox");
                GmailFolder receipts = (GmailFolder) store.getFolder("Receipts");
                inbox.open(Folder.READ_ONLY);
                receipts.open(Folder.READ_ONLY);
                Message[] foundMessages = receipts.getMessages();
                System.out.print(foundMessages.length);
                              
                WriteToFile writer = new WriteToFile();
                writer.writeMessagesToFile(foundMessages);
                
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

    }

