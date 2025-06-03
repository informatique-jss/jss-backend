package com.jss.osiris.modules.myjss.wordpress.model;

import java.io.Serializable;

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
@Table(indexes = { @Index(name = "idx_asso_mail_post", columnList = "id_mail,id_post", unique = true) })
public class AssoMailPost implements Serializable, IId {

        @Id
        @SequenceGenerator(name = "asso_mail_post_sequence", sequenceName = "asso_mail_post_sequence", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_mail_post_sequence")
        @JsonView({ JacksonViews.MyJssDetailedView.class })
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_mail")
        @IndexedField
        private Mail mail;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_post")
        @IndexedField
        private Post post;

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

        public Post getPost() {
                return post;
        }

        public void setPost(Post post) {
                this.post = post;
        }

}
