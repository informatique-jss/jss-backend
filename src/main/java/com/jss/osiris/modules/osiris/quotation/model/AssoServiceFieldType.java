package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_asso_service_field_type_service", columnList = "id_service") })
public class AssoServiceFieldType implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "asso_service_field_type_sequence", sequenceName = "asso_service_field_type_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_service_field_type_sequence")
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service")
    @IndexedField
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_field_type")
    @JsonView(JacksonViews.MyJssDetailedView.class)
    @IndexedField
    private ServiceFieldType serviceFieldType;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private String stringValue;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer integerValue;

    @JsonView(JacksonViews.MyJssDetailedView.class)
    private LocalDate dateValue;

    @ManyToOne
    @JoinColumn(name = "id_service_field_type_possible_value")
    @IndexedField
    @JsonView(JacksonViews.MyJssListView.class)
    private ServiceTypeFieldTypePossibleValue selectValue;

    @Column(columnDefinition = "TEXT")
    @JsonView(JacksonViews.MyJssListView.class)
    private String textAreaValue;

    @JsonView(JacksonViews.MyJssListView.class)
    private Boolean isMandatory;

    @Column(columnDefinition = "TEXT")
    @JsonView(JacksonViews.MyJssListView.class)
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

    public ServiceFieldType getServiceFieldType() {
        return serviceFieldType;
    }

    public void setServiceFieldType(ServiceFieldType serviceFieldType) {
        this.serviceFieldType = serviceFieldType;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(LocalDate dateValue) {
        this.dateValue = dateValue;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public String getTextAreaValue() {
        return textAreaValue;
    }

    public void setTextAreaValue(String textAreaValue) {
        this.textAreaValue = textAreaValue;
    }

    public ServiceTypeFieldTypePossibleValue getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(ServiceTypeFieldTypePossibleValue selectValue) {
        this.selectValue = selectValue;
    }

    public String getFormalisteComment() {
        return formalisteComment;
    }

    public void setFormalisteComment(String formalisteComment) {
        this.formalisteComment = formalisteComment;
    }

}
