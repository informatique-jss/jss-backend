package com.jss.osiris.modules.myjss.wordpress.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

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
@Table(indexes = {
                @Index(name = "idx_asso_provision_post", columnList = "id_provision,id_post", unique = true),
                @Index(name = "idx_asso_provision_newspaper", columnList = "id_provision,id_newspaper", unique = true) })
public class AssoProvisionPostNewspaper implements Serializable, IId {

        @Id
        @SequenceGenerator(name = "asso_asso_provision_post_newspaper_sequence", sequenceName = "asso_provision_post_newspaper_sequence", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_provision_post_newspaper_sequence")
        @JsonView({ JacksonViews.MyJssDetailedView.class })
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_provision")
        @IndexedField
        private Provision provision;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_post")
        @IndexedField
        private Post post;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_newspaper")
        @IndexedField
        private Newspaper newspaper;

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Provision getProvision() {
                return provision;
        }

        public void setProvision(Provision provision) {
                this.provision = provision;
        }

        public Post getPost() {
                return post;
        }

        public void setPost(Post post) {
                this.post = post;
        }

        public Newspaper getNewspaper() {
                return newspaper;
        }

        public void setNewspaper(Newspaper newspaper) {
                this.newspaper = newspaper;
        }
}
