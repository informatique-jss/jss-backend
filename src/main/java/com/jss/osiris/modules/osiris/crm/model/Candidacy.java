package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Candidacy implements Serializable, IId {

	@Id
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.MyJssDetailedView.class })
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.MyJssDetailedView.class })
	private String mail;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.MyJssDetailedView.class })
	private String searchedJob;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.MyJssDetailedView.class })
	private String message;

	@OneToMany(mappedBy = "candidacy", fetch = FetchType.LAZY)
	@JsonView(JacksonViews.OsirisListView.class)
	@JsonIgnoreProperties(value = { "candidacy" }, allowSetters = true)
	private List<Attachment> attachments;

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

	public String getSearchedJob() {
		return searchedJob;
	}

	public void setSearchedJob(String searchedJob) {
		this.searchedJob = searchedJob;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

}
