package com.jss.osiris.modules.quotation.model;

import java.time.LocalDate;

public class ActuLegaleAnnouncement {
    private Integer id;
    private Integer newspaperId;
    private String siren;
    private String companyName;
    private String comparnyAddress;
    private String companyZip;
    private String companyCity;
    private LocalDate parutionDate;
    private String departementParution;
    private String text;
    private boolean test = true;

    public Integer getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(Integer newspaperId) {
        this.newspaperId = newspaperId;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getComparnyAddress() {
        return comparnyAddress;
    }

    public void setComparnyAddress(String comparnyAddress) {
        this.comparnyAddress = comparnyAddress;
    }

    public String getCompanyZip() {
        return companyZip;
    }

    public void setCompanyZip(String companyZip) {
        this.companyZip = companyZip;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public LocalDate getParutionDate() {
        return parutionDate;
    }

    public void setParutionDate(LocalDate parutionDate) {
        this.parutionDate = parutionDate;
    }

    public String getDepartementParution() {
        return departementParution;
    }

    public void setDepartementParution(String departementParution) {
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

}
