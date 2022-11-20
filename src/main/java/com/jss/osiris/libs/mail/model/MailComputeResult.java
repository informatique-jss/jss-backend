package com.jss.osiris.libs.mail.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.jss.osiris.modules.miscellaneous.model.Mail;

@Entity
public class MailComputeResult {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_mail_sequence")
    private Integer id;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "asso_mail_compute_result_to", joinColumns = @JoinColumn(name = "id_mail_compute_result"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
    private List<Mail> recipientsMailTo;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "asso_mail_compute_result_cc", joinColumns = @JoinColumn(name = "id_mail_compute_result"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
    private List<Mail> recipientsMailCc;

    Boolean isSendToClient;
    Boolean isSendToAffaire;
    String mailToClientOrigin;
    String mailToAffaireOrigin;
    String mailCcAffaireOrigin;
    String mailCcClientOrigin;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Mail> getRecipientsMailTo() {
        return recipientsMailTo;
    }

    public void setRecipientsMailTo(List<Mail> recipientsMailTo) {
        this.recipientsMailTo = recipientsMailTo;
    }

    public List<Mail> getRecipientsMailCc() {
        return recipientsMailCc;
    }

    public void setRecipientsMailCc(List<Mail> recipientsMailCc) {
        this.recipientsMailCc = recipientsMailCc;
    }

}
