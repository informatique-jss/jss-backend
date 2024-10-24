package com.jss.osiris.modules.quotation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ActuLegaleAnnouncement {
    @JsonIgnore
    private Integer id;
    private Integer newspaperId;
    private Integer siren;
    private String companyName;
    private String companyAddress;
    private Integer companyZip;
    private String companyCity;
    private String parutionDate;
    private Integer departementParution;
    private String text;
    private boolean test = true;

    public Integer getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(Integer newspaperId) {
        this.newspaperId = newspaperId;
    }

    public Integer getSiren() {
        return siren;
    }

    public void setSiren(Integer siren) {
        this.siren = siren;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getCompanyZip() {
        return companyZip;
    }

    public void setCompanyZip(Integer companyZip) {
        this.companyZip = companyZip;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public Integer getDepartementParution() {
        return departementParution;
    }

    public void setDepartementParution(Integer departementParution) {
        this.departementParution = departementParution;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getParutionDate() {
        return parutionDate;
    }

    public void setParutionDate(String parutionDate) {
        this.parutionDate = parutionDate;
    }

}
