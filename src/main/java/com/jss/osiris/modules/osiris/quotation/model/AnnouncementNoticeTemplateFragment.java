package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class AnnouncementNoticeTemplateFragment implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "announcement_notice_template_fragment_sequence", sequenceName = "announcement_notice_template_fragment_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_notice_template_fragment_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String label;

	@Column(nullable = false, length = 40)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String code;

	@Column(columnDefinition = "TEXT")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String text;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Boolean isMultiple;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getIsMultiple() {
		return isMultiple;
	}

	public void setIsMultiple(Boolean isMultiple) {
		this.isMultiple = isMultiple;
	}
}
