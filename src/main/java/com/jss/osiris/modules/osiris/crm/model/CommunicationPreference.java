package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "idx_communication_preference_mail", columnList = "id_mail", unique = true) })
public class CommunicationPreference implements Serializable {

    @Id
    @SequenceGenerator(name = "communication_preference_sequence", sequenceName = "communication_preference_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "communication_preference_sequence")
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_mail", nullable = false)
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Mail mail;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Boolean isSubscribedToNewspaperNewletter;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Boolean isSubscribedToCorporateNewsletter;

    private String validationToken;

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public Boolean getIsSubscribedToNewspaperNewletter() {
        return isSubscribedToNewspaperNewletter;
    }

    public void setIsSubscribedToNewspaperNewletter(Boolean isSubscribedToNewspaperNewletter) {
        this.isSubscribedToNewspaperNewletter = isSubscribedToNewspaperNewletter;
    }

    public Boolean getIsSubscribedToCorporateNewsletter() {
        return isSubscribedToCorporateNewsletter;
    }

    public void setIsSubscribedToCorporateNewsletter(Boolean isSubscribedToCorporateNewsletter) {
        this.isSubscribedToCorporateNewsletter = isSubscribedToCorporateNewsletter;
    }

    public String getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

}
