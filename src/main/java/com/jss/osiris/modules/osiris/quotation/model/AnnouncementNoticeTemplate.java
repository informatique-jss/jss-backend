package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class AnnouncementNoticeTemplate implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "announcement_notice_template_sequence", sequenceName = "announcement_notice_template_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_notice_template_sequence")
	private Integer id;

	@Column(nullable = false, length = 250)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String label;

	@Column(nullable = false, length = 40)
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String code;

	@Column(columnDefinition = "TEXT")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private String text;

	@ManyToMany
	@JoinTable(name = "asso_annoucement_notice_template_provision_type", joinColumns = @JoinColumn(name = "id_announcement_notice_template"), inverseJoinColumns = @JoinColumn(name = "id_provision_family_type"))
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<ProvisionFamilyType> provisionFamilyTypes;

	@ManyToMany
	@JoinTable(name = "asso_announcement_notice_template_announcement_fragment", joinColumns = @JoinColumn(name = "id_announcement_notice_template"), inverseJoinColumns = @JoinColumn(name = "id_announcement_notice_template_fragment"))
	@JsonView(JacksonViews.MyJssDetailedView.class)
	private List<AnnouncementNoticeTemplateFragment> announcementNoticeTemplateFragments;

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

	public List<ProvisionFamilyType> getProvisionFamilyTypes() {
		return provisionFamilyTypes;
	}

	public void setProvisionFamilyTypes(List<ProvisionFamilyType> provisionFamilyTypes) {
		this.provisionFamilyTypes = provisionFamilyTypes;
	}

	public List<AnnouncementNoticeTemplateFragment> getAnnouncementNoticeTemplateFragments() {
		return announcementNoticeTemplateFragments;
	}

	public void setAnnouncementNoticeTemplateFragments(
			List<AnnouncementNoticeTemplateFragment> announcementNoticeTemplateFragments) {
		this.announcementNoticeTemplateFragments = announcementNoticeTemplateFragments;
	}
}
