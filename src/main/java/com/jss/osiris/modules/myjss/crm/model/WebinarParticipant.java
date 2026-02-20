package com.jss.osiris.modules.myjss.crm.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.crm.model.Webinar;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "idx_webinar_participant_mail", columnList = "id_mail") })
public class WebinarParticipant implements Serializable {
    @Id
    @SequenceGenerator(name = "webinar_participant_sequence", sequenceName = "webinar_participant_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webinar_participant_sequence")
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private Integer id;

    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    @Column(length = 50)
    private String firstname;

    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    @Column(length = 50)
    private String lastname;

    @ManyToOne
    @JoinColumn(name = "id_mail", nullable = false)
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private Mail mail;

    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private String phoneNumber;

    private Boolean isParticipating;

    @Column(columnDefinition = "TEXT")
    private String question;

    @ManyToOne
    @JoinTable(name = "asso_webinar_participant", joinColumns = @JoinColumn(name = "id_webinar_participant"), inverseJoinColumns = @JoinColumn(name = "id_webinar"))
    @IndexedField
    @JsonView(JacksonViews.OsirisListView.class)
    private Webinar webinar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsParticipating() {
        return isParticipating;
    }

    public void setIsParticipating(Boolean isParticipating) {
        this.isParticipating = isParticipating;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public Webinar getWebinar() {
        return webinar;
    }

    public void setWebinar(Webinar webinar) {
        this.webinar = webinar;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

}
