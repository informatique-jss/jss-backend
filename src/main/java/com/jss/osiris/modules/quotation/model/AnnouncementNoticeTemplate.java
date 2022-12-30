package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class AnnouncementNoticeTemplate implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "announcement_notice_template_sequence", sequenceName = "announcement_notice_template_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_notice_template_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 40)
	private String code;

	@Column(columnDefinition = "TEXT")
	private String text;

	@ManyToMany
	@JoinTable(name = "asso_annoucement_notice_template_provision_type", joinColumns = @JoinColumn(name = "id_announcement_notice_template"), inverseJoinColumns = @JoinColumn(name = "id_provision_family_type"))
	private List<ProvisionFamilyType> provisionFamilyTypes;

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

}
