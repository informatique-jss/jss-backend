package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
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

	@OneToOne(mappedBy = "mail")
	@JsonIgnore
	private Responsable responsable;

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

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

}
