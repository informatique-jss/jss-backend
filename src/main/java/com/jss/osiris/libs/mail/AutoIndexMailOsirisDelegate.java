package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.jss.osiris.libs.exception.OsirisException;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;

@org.springframework.stereotype.Service
public class AutoIndexMailOsirisDelegate {

    @Value("${mail.imap.host}")
    private String mailImapHost;
    @Value("mail.imap.port")
    private String mailImapPort;
    @Value("mail.imap.username")
    private String mailImapUsername;
    @Value("mail.imap.password")
    private String mailImapPassword;
    @Value("mail.imap.auth")
    private String mailIpmapAuth;
    @Value("mail.imap.ssl.enable")
    private String mailImapEnable;

    private Session connectionOutlookOsiris() {
        Properties properties = new Properties();
        properties.put("mail.imap.host", mailImapHost);
        properties.put("mail.imap.port", mailImapPort);
        properties.put("mail.imap.ssl.enable", mailImapEnable);
        properties.put("mail.imap.auth", mailIpmapAuth);

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailImapUsername, mailImapPassword);
            }
        });
    }

    private Document exportMailToPdf(Message message) throws OsirisException {
        try {
            String subject = message.getSubject();
            Address[] fromAddresses = message.getFrom();
            Address[] toAddresses = message.getReplyTo();
            Date receivedDate = message.getReceivedDate();
            String from = fromAddresses[0].toString();
            String to = toAddresses[0].toString();

            String body = "";
            if (message.getContent() != null) {
                if (message.isMimeType("text/plain"))
                    body = (String) message.getContent();
                else if (message.isMimeType("text/html") && message.getContent() != null)
                    body = (String) message.getContent();
            }

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Mail_" + subject + ".pdf"));
            document.open();
            document.add(new Paragraph("Sujet: " + subject));
            document.add(new Paragraph("De: " + from));
            document.add(new Paragraph("A: " + to));
            document.add(new Paragraph("Date de réception: " + receivedDate));
            document.add(new Paragraph("Mail:"));
            document.add(new Paragraph(body));
            document.close();
            File file = new File("Mail_" + subject + ".pdf");
            String fileName = file.getName();
            return document;

        } catch (IOException e) {
            throw new OsirisException(e, "no content in mail");
        } catch (MessagingException e) {
            throw new OsirisException(e, "wrong login or password");
        } catch (DocumentException e) {
            throw new OsirisException(e, "impossible to generate pdf document for export");
        }
    }

    private Boolean purgeInboxMailOsiris(Message message) throws OsirisException {
        LocalDate purgeDate = LocalDate.now().minus(30, ChronoUnit.DAYS);
        Date sentDate;
        try {
            sentDate = message.getReceivedDate();
            if (sentDate != null) {
                LocalDate messageDate = convertToLocalDate(sentDate);

                if (messageDate.isBefore(purgeDate)) {
                    message.setFlag(Flags.Flag.DELETED, true);
                    return true;
                }
            }
        } catch (MessagingException e) {
            throw new OsirisException(e, "no received date in mail");
        }
        return false;
    }

    private static LocalDate convertToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public List<Document> getPdfMailsFromJavaMailImap() throws OsirisException {
        List<Document> mailExports = new ArrayList<>();

        Session session = connectionOutlookOsiris();
        Store store;
        Folder folder;
        try {
            store = session.getStore("imap");
        } catch (NoSuchProviderException e) {
            throw new OsirisException(e, "no imap store");
        }
        try {
            store.connect(mailImapHost, mailImapUsername, mailImapPassword);
            // open box mail reception
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // get all mails
            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                if (!purgeInboxMailOsiris(message))
                    mailExports.add(exportMailToPdf(message));
            }

            folder.close(false);
            store.close();
        } catch (MessagingException e) {
            throw new OsirisException(e, "wrong login or password");
        }
        return mailExports;
    }
}