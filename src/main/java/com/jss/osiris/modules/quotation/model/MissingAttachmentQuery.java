package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

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

    @ManyToMany
    @JoinTable(name = "asso_service_field_type_missing_attachment_query", joinColumns = @JoinColumn(name = "id_missing_attchment_query"), inverseJoinColumns = @JoinColumn(name = "id_asso_service_field_type"))
    @JsonIgnoreProperties(value = { "service" }, allowSetters = true)
    private List<AssoServiceFieldType> assoServiceFieldType;

    @Column(columnDefinition = "TEXT")
    private String comment;
    private Boolean sendToMe;
    private Boolean copyToMe;
    private LocalDateTime createdDateTime;

    private LocalDateTime firstCustomerReminderDateTime;
    private LocalDateTime secondCustomerReminderDateTime;
    private LocalDateTime thirdCustomerReminderDateTime;

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

    public LocalDateTime getFirstCustomerReminderDateTime() {
        return firstCustomerReminderDateTime;
    }

    public void setFirstCustomerReminderDateTime(LocalDateTime firstCustomerReminderDateTime) {
        this.firstCustomerReminderDateTime = firstCustomerReminderDateTime;
    }

    public LocalDateTime getSecondCustomerReminderDateTime() {
        return secondCustomerReminderDateTime;
    }

    public void setSecondCustomerReminderDateTime(LocalDateTime secondCustomerReminderDateTime) {
        this.secondCustomerReminderDateTime = secondCustomerReminderDateTime;
    }

    public LocalDateTime getThirdCustomerReminderDateTime() {
        return thirdCustomerReminderDateTime;
    }

    public void setThirdCustomerReminderDateTime(LocalDateTime thirdCustomerReminderDateTime) {
        this.thirdCustomerReminderDateTime = thirdCustomerReminderDateTime;
    }

    public List<AssoServiceFieldType> getAssoServiceFieldType() {
        return assoServiceFieldType;
    }

    public void setAssoServiceFieldType(List<AssoServiceFieldType> assoServiceFieldType) {
        this.assoServiceFieldType = assoServiceFieldType;
    }
}
