package com.jss.osiris.libs.mail.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class MailComputeResult implements Serializable {
    @Id
    @SequenceGenerator(name = "customer_mail_sequence", sequenceName = "customer_mail_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_mail_sequence")
    private Integer id;

    @ManyToMany
    @JoinTable(name = "asso_mail_compute_result_to", joinColumns = @JoinColumn(name = "id_mail_compute_result"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private List<Mail> recipientsMailTo;

    @ManyToMany
    @JoinTable(name = "asso_mail_compute_result_cc", joinColumns = @JoinColumn(name = "id_mail_compute_result"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
    @JsonView(JacksonViews.MyJssDetailedView.class)
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
