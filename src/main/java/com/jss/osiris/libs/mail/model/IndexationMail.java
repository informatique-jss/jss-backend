package com.jss.osiris.libs.mail.model;

import java.io.InputStream;

public class IndexationMail {

    private Integer id;

    private InputStream mailPdf;

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

    public InputStream getMailPdf() {
        return mailPdf;
    }

    public void setMailPdf(InputStream mailPdf) {
        this.mailPdf = mailPdf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
