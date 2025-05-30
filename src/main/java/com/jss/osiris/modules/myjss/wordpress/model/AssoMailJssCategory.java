package com.jss.osiris.modules.myjss.wordpress.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_asso_mail_jss_category_mail", columnList = "id_mail"),
        @Index(name = "idx_asso_mail_jss_category_category", columnList = "id_jss_category") })
public class AssoMailJssCategory implements Serializable, IId {
    @Id
    @SequenceGenerator(name = "asso_mail_jss_category_sequence", sequenceName = "asso_mail_jss_category_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_mail_jss_category_sequence")
    @JsonView({ JacksonViews.MyJssDetailedView.class })
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mail")
    @IndexedField
    @JsonView({ JacksonViews.MyJssDetailedView.class })
    private Mail mail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jss_category")
    @IndexedField
    @JsonView({ JacksonViews.MyJssDetailedView.class })
    private JssCategory jssCategory;

    @JsonView({ JacksonViews.MyJssDetailedView.class })
    private LocalDateTime lastConsultationDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public JssCategory getJssCategory() {
        return jssCategory;
    }

    public void setJssCategory(JssCategory jssCategory) {
        this.jssCategory = jssCategory;
    }

    public LocalDateTime getLastConsultationDate() {
        return lastConsultationDate;
    }

    public void setLastConsultationDate(LocalDateTime lastConsultationDate) {
        this.lastConsultationDate = lastConsultationDate;
    }
}
