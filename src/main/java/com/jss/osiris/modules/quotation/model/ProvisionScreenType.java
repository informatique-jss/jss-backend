package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

import com.jss.osiris.modules.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class ProvisionScreenType implements Serializable, IId {

    public static String DOMICILIATION = "DOMICILIATION";
    public static String ANNOUNCEMENT = "ANNOUNCEMENT";
    public static String FORMALITE = "FORMALITE";
    public static String STANDARD = "STANDARD";

    @Id
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Integer id;

    @Column(nullable = false)
    private String label;

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
