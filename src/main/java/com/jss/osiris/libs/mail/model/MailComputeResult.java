package com.jss.osiris.libs.mail.model;

import java.util.ArrayList;

import com.jss.osiris.modules.miscellaneous.model.Mail;

public class MailComputeResult {
    private ArrayList<Mail> recipientsMailTo;
    private ArrayList<Mail> recipientsMailCc;
    Boolean isSendToClient;
    Boolean isSendToAffaire;
    String mailToClientOrigin;
    String mailToAffaireOrigin;
    String mailCcAffaireOrigin;
    String mailCcClientOrigin;

    public ArrayList<Mail> getRecipientsMailTo() {
        return recipientsMailTo;
    }

    public void setRecipientsMailTo(ArrayList<Mail> recipientsMailTo) {
        this.recipientsMailTo = recipientsMailTo;
    }

    public ArrayList<Mail> getRecipientsMailCc() {
        return recipientsMailCc;
    }

    public void setRecipientsMailCc(ArrayList<Mail> recipientsMailCc) {
        this.recipientsMailCc = recipientsMailCc;
    }

    public Boolean getIsSendToClient() {
        return isSendToClient;
    }

    public void setIsSendToClient(Boolean isSendToClient) {
        this.isSendToClient = isSendToClient;
    }

    public Boolean getIsSendToAffaire() {
        return isSendToAffaire;
    }

    public void setIsSendToAffaire(Boolean isSendToAffaire) {
        this.isSendToAffaire = isSendToAffaire;
    }

    public String getMailToClientOrigin() {
        return mailToClientOrigin;
    }

    public void setMailToClientOrigin(String mailToClientOrigin) {
        this.mailToClientOrigin = mailToClientOrigin;
    }

    public String getMailToAffaireOrigin() {
        return mailToAffaireOrigin;
    }

    public void setMailToAffaireOrigin(String mailToAffaireOrigin) {
        this.mailToAffaireOrigin = mailToAffaireOrigin;
    }

    public String getMailCcAffaireOrigin() {
        return mailCcAffaireOrigin;
    }

    public void setMailCcAffaireOrigin(String mailCcAffaireOrigin) {
        this.mailCcAffaireOrigin = mailCcAffaireOrigin;
    }

    public String getMailCcClientOrigin() {
        return mailCcClientOrigin;
    }

    public void setMailCcClientOrigin(String mailCcClientOrigin) {
        this.mailCcClientOrigin = mailCcClientOrigin;
    }

}
