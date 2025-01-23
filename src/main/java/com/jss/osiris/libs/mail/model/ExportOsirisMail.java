package com.jss.osiris.libs.mail.model;

import java.io.InputStream;

public class ExportOsirisMail {

    private byte[] exportedMail;

    private InputStream mailContent;

    private String subjectMail;

    private String fileName;

    public byte[] getExportedMail() {
        return exportedMail;
    }

    public void setExportedMail(byte[] file) {
        this.exportedMail = file;
    }

    public String getSubjectMail() {
        return subjectMail;
    }

    public void setSubjectMail(String subjectMail) {
        this.subjectMail = subjectMail;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getMailContent() {
        return mailContent;
    }

    public void setMailContent(InputStream mailContent) {
        this.mailContent = mailContent;
    }

}
