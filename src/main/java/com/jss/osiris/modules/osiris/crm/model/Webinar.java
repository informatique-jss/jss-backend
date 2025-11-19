package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class Webinar implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "webinar_sequence", sequenceName = "webinar_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webinar_sequence")
	@JsonView(JacksonViews.OsirisListView.class)
	private Integer id;

	@Column(nullable = false)
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.MyJssDetailedView.class })
	private String label;

	@Column(columnDefinition = "TEXT")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.MyJssDetailedView.class })
	private String description;

	private String code;

	@JsonView(JacksonViews.MyJssDetailedView.class)
	private LocalDateTime webinarDate;

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

	public LocalDateTime getWebinarDate() {
		return webinarDate;
	}

	public void setWebinarDate(LocalDateTime webinarDate) {
		this.webinarDate = webinarDate;
	}
}
