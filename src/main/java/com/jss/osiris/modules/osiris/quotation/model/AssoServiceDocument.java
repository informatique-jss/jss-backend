package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;

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

@Entity
@Table(indexes = { @Index(name = "idx_asso_service_document", columnList = "id_service") })
public class AssoServiceDocument implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "asso_service_document_sequence", sequenceName = "asso_service_document_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_document_sequence")
	@JsonView(JacksonViews.MyJssView.class)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_service")
	@IndexedField
	private Service service;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_type_document")
	@IndexedField
	@JsonView(JacksonViews.MyJssView.class)
	private TypeDocument typeDocument;

	@OneToMany(mappedBy = "assoServiceDocument", fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value = { "assoServiceDocument" }, allowSetters = true)
	private List<Attachment> attachments;

	@JsonView(JacksonViews.MyJssView.class)
	private Boolean isMandatory;

	@Column(columnDefinition = "TEXT")
	@JsonView(JacksonViews.MyJssView.class)
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
