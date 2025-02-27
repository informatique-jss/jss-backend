package com.jss.osiris.libs.mail.model;

import java.io.InputStream;

public class IndexationMail {

    private InputStream mailText;

    private String subject;

    private String fileName;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subjectMail) {
        this.subject = subjectMail;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getMailText() {
        return mailText;
    }

    public void setMailText(InputStream mailPdf) {
        this.mailText = mailPdf;
    }

}
