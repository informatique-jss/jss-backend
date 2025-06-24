package com.jss.osiris.modules.myjss.wordpress.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_reading_folder", columnList = "id", unique = true),
        @Index(name = "idx_reading_folder_mail", columnList = "id_mail"),
})
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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "asso_reading_folder_post", joinColumns = @JoinColumn(name = "id_reading_folder"), inverseJoinColumns = @JoinColumn(name = "id_post"))
    private List<Post> posts;

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

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

}
