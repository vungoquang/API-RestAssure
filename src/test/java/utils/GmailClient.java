package utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GmailClient {
    Properties loadData;
    private Store store;
    String SUBJECT = "Portfolio Verification Code";
    String BODY = "Verification Code";
    String RECIPIENT = "automation.createtoken@gmail.com";

    public GmailClient() throws IOException {
        this.loadData = utils.PropertyUtils.readPropertiesFile("src/test/java/data/data.properties");
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.host", "smtp");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtps.timeout", "1000");
        properties.setProperty("mail.smtp.connectiontimeout", "1000");
        properties.setProperty("http_proxy", "http://squid:3128");
        properties.setProperty("https_porxy", "http://squid:3128");

        Session session = Session.getDefaultInstance(properties, null);

        try {
            store = session.getStore("imaps");
            String GMAIL_USER = loadData.getProperty("Email");
            String GMAIL_PASSWORD = loadData.getProperty("Password");
            store.connect("smtp.gmail.com", GMAIL_USER, GMAIL_PASSWORD);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public static String getMFACode(String subject, String body, String recipient) throws MessagingException, IOException, InterruptedException {
        Thread.sleep(30000);
        GmailClient gmailClient = new GmailClient();
        //create the folder object and open it
        Folder inbox = gmailClient.store.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);

        Message[] messages = inbox.search(getSearchTerm(subject, body, recipient));
        if (messages.length > 0) {
            System.out.println("FOUND " + messages.length + " email!");
        }
        String result = "";

        for (Message message : messages) {
            System.out.println("Subject: " + message.getSubject());
            String html = getTextFromMessage(message);
            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
        }

        inbox.close(true);
        gmailClient.store.close();
        return findIntegers(result);
    }

    private static SearchTerm getSearchTerm(String subject, String body, String recipient) throws AddressException {
        Flags seen = new Flags(Flags.Flag.SEEN);
        SearchTerm unreadTerm = new FlagTerm(seen, false);

        Flags recent = new Flags(Flags.Flag.RECENT);
        SearchTerm recentTerm = new FlagTerm(recent, true);

        DateTime rightNow = new DateTime();
        DateTime past = rightNow.minusWeeks(30);

        SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LE, rightNow.toDate());
        SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GE, past.toDate());

        SearchTerm lastHourMails = new AndTerm(newerThan, olderThan);
        SubjectTerm subjectTerm = new SubjectTerm(subject);
        BodyTerm bodyTerm = new BodyTerm(body);

        SearchTerm contentTerm = new AndTerm(subjectTerm, bodyTerm);
        SearchTerm toTerm = new RecipientTerm(Message.RecipientType.TO, new InternetAddress(recipient));

        SearchTerm searchTerm = new AndTerm(contentTerm, toTerm);
        SearchTerm searchTermLastHours = new AndTerm(searchTerm, lastHourMails);
        return new AndTerm(searchTermLastHours, unreadTerm);
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    static String findIntegers(String stringToSearch) {
        Pattern integerPattern = Pattern.compile("-?\\d+");
        Matcher matcher = integerPattern.matcher(stringToSearch);

        List<String> integerList = new ArrayList<>();
        while (matcher.find()) {
            integerList.add(matcher.group());
        }
        return integerList.get(0);
    }

    public static void main(String[] args) throws MessagingException, IOException, InterruptedException {
        GmailClient mail= new GmailClient();
        String test = getMFACode(mail.SUBJECT, mail.BODY, mail.RECIPIENT);
        System.out.println(test);
    }

}
