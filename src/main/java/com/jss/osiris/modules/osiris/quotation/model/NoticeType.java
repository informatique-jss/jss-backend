package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class NoticeType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "notice_type_sequence", sequenceName = "notice_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notice_type_sequence")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private Integer id;

	@Column(nullable = false, length = 200)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private String label;

	@Column(nullable = false, length = 20)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_notice_type_family")
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class })
	private NoticeTypeFamily noticeTypeFamily;

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

	public NoticeTypeFamily getNoticeTypeFamily() {
		return noticeTypeFamily;
	}

	public void setNoticeTypeFamily(NoticeTypeFamily noticeTypeFamily) {
		this.noticeTypeFamily = noticeTypeFamily;
	}

}
