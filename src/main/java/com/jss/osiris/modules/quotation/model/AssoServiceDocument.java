package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;

@Entity
@Table(indexes = { @Index(name = "idx_asso_service_document", columnList = "id_service") })
public class AssoServiceDocument implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_service_document_sequence", sequenceName = "asso_service_document_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_document_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service")
	@IndexedField
	private Service service;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_type_document")
	@IndexedField
	private TypeDocument typeDocument;

	@OneToMany(mappedBy = "assoServiceDocument", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties(value = { "assoServiceDocument" }, allowSetters = true)
	private List<Attachment> attachments;

	private Boolean isMandatory;

	@Column(columnDefinition = "TEXT")
	private String formalisteComment;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public TypeDocument getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(TypeDocument typeDocument) {
		this.typeDocument = typeDocument;
	}

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getFormalisteComment() {
		return formalisteComment;
	}

	public void setFormalisteComment(String formalisteComment) {
		this.formalisteComment = formalisteComment;
	}

}
