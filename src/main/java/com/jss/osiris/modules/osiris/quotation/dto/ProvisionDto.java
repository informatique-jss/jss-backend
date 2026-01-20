package com.jss.osiris.modules.osiris.quotation.dto;

import java.time.LocalDate;

public class ProvisionDto {
    private long id;
    private LocalDate productionDate;
    private long customerOrderId;
    private String responsable;
    private String tiers;
    private String affaire;
    private String service;
    private String confrere;
    private String formalisteEmployee;
    private String provisionLabel;
    private String competentAuthority;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public long getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(long customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getTiers() {
        return tiers;
    }

    public void setTiers(String tiers) {
        this.tiers = tiers;
    }

    public String getAffaire() {
        return affaire;
    }

    public void setAffaire(String affaire) {
        this.affaire = affaire;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getConfrere() {
        return confrere;
    }

    public void setConfrere(String confrere) {
        this.confrere = confrere;
    }

    public String getFormalisteEmployee() {
        return formalisteEmployee;
    }

    public void setFormalisteEmployee(String formalisteEmployee) {
        this.formalisteEmployee = formalisteEmployee;
    }

    public String getProvisionLabel() {
        return provisionLabel;
    }

    public void setProvisionLabel(String provisionLabel) {
        this.provisionLabel = provisionLabel;
    }

    public String getCompetentAuthority() {
        return competentAuthority;
    }

    public void setCompetentAuthority(String competentAuthority) {
        this.competentAuthority = competentAuthority;
    }
}