package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailAuthor;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_mail_mail", columnList = "mail") })
public class Mail implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "mail_sequence", sequenceName = "mail_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mail_sequence")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@Column(nullable = false)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String mail;

	@OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "mail" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private List<AssoMailAuthor> assoMailAuthors;

	@OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "mail" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private List<AssoMailTag> assoMailTags;

	@OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "mail" }, allowSetters = true)
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	private List<AssoMailJssCategory> assoMailJssCategories;

	public Mail() {
	}

	public Mail(String mail) {
		this.setMail(mail);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<AssoMailAuthor> getAssoMailAuthors() {
		return assoMailAuthors;
	}

	public void setAssoMailAuthors(List<AssoMailAuthor> assoMailAuthors) {
		this.assoMailAuthors = assoMailAuthors;
	}

	public List<AssoMailTag> getAssoMailTags() {
		return assoMailTags;
	}

	public void setAssoMailTags(List<AssoMailTag> assoMailTags) {
		this.assoMailTags = assoMailTags;
	}

	public List<AssoMailJssCategory> getAssoMailJssCategories() {
		return assoMailJssCategories;
	}

	public void setAssoMailJssCategories(List<AssoMailJssCategory> assoMailJssCategories) {
		this.assoMailJssCategories = assoMailJssCategories;
	}

}
