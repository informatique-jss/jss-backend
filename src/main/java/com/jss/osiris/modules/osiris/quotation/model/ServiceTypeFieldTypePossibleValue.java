package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ServiceTypeFieldTypePossibleValue implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "service_type_field_type_possible_value_sequence", sequenceName = "service_type_field_type_possible_value_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_type_field_type_possible_value_sequence")
    @JsonView(JacksonViews.MyJssView.class)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service_field_type")
    private ServiceFieldType serviceFieldType;

    @JsonView(JacksonViews.MyJssView.class)
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ServiceFieldType getServiceFieldType() {
        return serviceFieldType;
    }

    public void setServiceFieldType(ServiceFieldType serviceFieldType) {
        this.serviceFieldType = serviceFieldType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
