package com.jss.osiris.libs.mail.model;

import java.io.FileOutputStream;

public class ExportOsirisMail {

    private FileOutputStream exportedMail;

    private String subjectMail;

    private String fileName;

    public FileOutputStream getExportedMail() {
        return exportedMail;
    }

    public void setExportedMail(FileOutputStream file) {
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

}
