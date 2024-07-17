package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.IId;

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
public class AssoRadioValueServiceTypeFieldType implements Serializable, IId {
    @Id
    @SequenceGenerator(name = "asso_radio_value_service_type_field_type_sequence", sequenceName = "asso_radio_value_service_type_field_type_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_radio_value_service_type_field_type_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asso_service_type_field_type")
    @IndexedField
    private AssoServiceTypeFieldType assoServiceTypeFieldType;

    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AssoServiceTypeFieldType getAssoServiceTypeFieldType() {
        return assoServiceTypeFieldType;
    }

    public void setAssoServiceTypeFieldType(AssoServiceTypeFieldType assoServiceTypeFieldType) {
        this.assoServiceTypeFieldType = assoServiceTypeFieldType;
    }
}
