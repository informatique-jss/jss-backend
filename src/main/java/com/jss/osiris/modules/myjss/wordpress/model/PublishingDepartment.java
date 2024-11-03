package com.jss.osiris.modules.myjss.wordpress.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class PublishingDepartment {

    @Id
    private Integer id;
    private String name;
    private String code;

    @Transient
    private AcfDepartment acf;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcfDepartment getAcf() {
        return acf;
    }

    public void setAcf(AcfDepartment acf) {
        this.acf = acf;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
