package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.IndexedField;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Civility implements Serializable, IId {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "civility_sequence", sequenceName = "civility_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "civility_sequence")
    private Integer id;

    @Column(nullable = false)
    @IndexedField
    private String label;

    @Column(nullable = false, length = 20)
    private String code;

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

}
