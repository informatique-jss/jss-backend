package com.jss.osiris.modules.myjss.crm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.crm.model.Webinar;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class WebinarParticipant implements IId {
    @Id
    @SequenceGenerator(name = "webinar_participant_sequence", sequenceName = "webinar_participant_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webinar_participant_sequence")
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private Integer id;

    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private String firstname;

    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private String lastname;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mail", nullable = false)
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private Mail mail;

    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private String phoneNumber;
    private Boolean isParticipating;

    @ManyToMany
    @JoinTable(name = "asso_webinar_participant", joinColumns = @JoinColumn(name = "id_webinar_participant"), inverseJoinColumns = @JoinColumn(name = "id_webinar"))
    @IndexedField
    @JsonView(JacksonViews.OsirisListView.class)
    private List<Webinar> webinars;

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

    public List<Webinar> getWebinars() {
        return webinars;
    }

    public void setWebinars(List<Webinar> webinars) {
        this.webinars = webinars;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

}
