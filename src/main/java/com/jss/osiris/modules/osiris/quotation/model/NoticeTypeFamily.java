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
public class NoticeTypeFamily implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "notice_type_family_sequence", sequenceName = "notice_type_family_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_type_family_sequence")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private Integer id;

	@Column(nullable = false, length = 100)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
			JacksonViews.OsirisDetailedView.class })
	private String label;

	@Column(nullable = false, length = 20)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String code;

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

}
