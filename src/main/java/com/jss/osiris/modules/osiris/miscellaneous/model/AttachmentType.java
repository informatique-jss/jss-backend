package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class AttachmentType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "attachment_type_sequence", sequenceName = "attachment_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_type_sequence")
	@JsonView(JacksonViews.MyJssView.class)
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView(JacksonViews.MyJssView.class)
	private String label;

	@Column(nullable = false, length = 20)
	@JsonView(JacksonViews.MyJssView.class)
	private String code;

	@Column(length = 400)
	private String description;

	private Boolean isToSentOnUpload;

	private Boolean isToSentOnFinalizationMail;

	private Boolean isHiddenFromUser;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsToSentOnUpload() {
		return isToSentOnUpload;
	}

	public void setIsToSentOnUpload(Boolean isToSentOnUpload) {
		this.isToSentOnUpload = isToSentOnUpload;
	}

	public Boolean getIsToSentOnFinalizationMail() {
		return isToSentOnFinalizationMail;
	}

	public void setIsToSentOnFinalizationMail(Boolean isToSentOnFinalizationMail) {
		this.isToSentOnFinalizationMail = isToSentOnFinalizationMail;
	}

	public Boolean getIsHiddenFromUser() {
		return isHiddenFromUser;
	}

	public void setIsHiddenFromUser(Boolean isHiddenFromUser) {
		this.isHiddenFromUser = isHiddenFromUser;
	}

}
