package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(indexes = { @Index(name = "idx_service_missing_attachment_query", columnList = "id_service") })
public class MissingAttachmentQuery {
    @Id
    @SequenceGenerator(name = "missing_attachment_query_sequence", sequenceName = "missing_attachment_query_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "missing_attachment_query_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service")
    private Service service;

    @ManyToMany
    @JoinTable(name = "asso_service_document_missing_attachment_query", joinColumns = @JoinColumn(name = "id_missing_attchment_query"), inverseJoinColumns = @JoinColumn(name = "id_asso_service_document"))
    @JsonIgnoreProperties(value = { "service" }, allowSetters = true)
    private List<AssoServiceDocument> assoServiceDocument;

    private String comment;
    private Boolean sendToMe;
    private Boolean copyToMe;
    private LocalDateTime createdDateTime;

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

    public List<AssoServiceDocument> getAssoServiceDocument() {
        return assoServiceDocument;
    }

    public void setAssoServiceDocument(List<AssoServiceDocument> assoServiceDocument) {
        this.assoServiceDocument = assoServiceDocument;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getSendToMe() {
        return sendToMe;
    }

    public void setSendToMe(Boolean sendToMe) {
        this.sendToMe = sendToMe;
    }

    public Boolean getCopyToMe() {
        return copyToMe;
    }

    public void setCopyToMe(Boolean copyToMe) {
        this.copyToMe = copyToMe;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

}
