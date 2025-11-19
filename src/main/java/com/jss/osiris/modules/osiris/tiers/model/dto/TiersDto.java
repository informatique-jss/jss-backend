package com.jss.osiris.modules.osiris.tiers.model.dto;

import java.util.HashMap;

public class TiersDto {

    private String denomination;
    private Integer id;
    private HashMap<String, String> kpiValues;
    private String salesEmployee;
    private String formalisteEmployee;
    private String isNewTiers;
    private String address;
    private String tiersCategory;

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(String salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public String getFormalisteEmployee() {
        return formalisteEmployee;
    }

    public void setFormalisteEmployee(String formalisteEmployee) {
        this.formalisteEmployee = formalisteEmployee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTiersCategory() {
        return tiersCategory;
    }

    public void setTiersCategory(String tiersCategory) {
        this.tiersCategory = tiersCategory;
    }

    public String getIsNewTiers() {
        return isNewTiers;
    }

    public void setIsNewTiers(String isNewTiers) {
        this.isNewTiers = isNewTiers;
    }

    public HashMap<String, String> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(HashMap<String, String> kpiValues) {
        this.kpiValues = kpiValues;
    }

}
