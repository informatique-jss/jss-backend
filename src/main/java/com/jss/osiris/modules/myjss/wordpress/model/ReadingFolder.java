package com.jss.osiris.modules.myjss.wordpress.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ReadingFolder implements Serializable {
    @Id
    @SequenceGenerator(name = "reading_folder_sequence", sequenceName = "reading_folder_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reading_folder_sequence")
    @JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
    private Integer id;

    @JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
    private String label;

    @ManyToOne
    @JoinColumn(name = "id_mail", nullable = false)
    private Mail mail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media")
    @JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
    private Media media;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

}
