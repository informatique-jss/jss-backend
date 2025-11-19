package com.jss.osiris.modules.osiris.tiers.model.dto;

import java.util.HashMap;

public class ResponsableDto {

    private String firstname;
    private String lastname;
    private Integer id;
    private HashMap<String, String> kpiValues;
    private Integer tiersId;
    private String tiersDenomination;
    private String tiersCategory;
    private String responsableCategory;
    private String salesEmployee;

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

    public HashMap<String, String> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(HashMap<String, String> kpiValues) {
        this.kpiValues = kpiValues;
    }

    public Integer getTiersId() {
        return tiersId;
    }

    public void setTiersId(Integer tiersId) {
        this.tiersId = tiersId;
    }

    public String getTiersDenomination() {
        return tiersDenomination;
    }

    public void setTiersDenomination(String tiersDenomination) {
        this.tiersDenomination = tiersDenomination;
    }

    public String getTiersCategory() {
        return tiersCategory;
    }

    public void setTiersCategory(String tiersCategory) {
        this.tiersCategory = tiersCategory;
    }

    public String getResponsableCategory() {
        return responsableCategory;
    }

    public void setResponsableCategory(String responsableCategory) {
        this.responsableCategory = responsableCategory;
    }

    public String getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(String salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

}
