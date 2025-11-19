package com.jss.osiris.modules.osiris.tiers.model.dto;

import java.math.BigDecimal;
import java.util.HashMap;

public class ResponsableDto {

    private String firstname;
    private String lastname;
    private Integer id;
    private HashMap<String, BigDecimal> kpiValues;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HashMap<String, BigDecimal> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(HashMap<String, BigDecimal> kpiValues) {
        this.kpiValues = kpiValues;
    }

}
