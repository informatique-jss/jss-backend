package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RneResult {
    private List<RneCompany> companies;
    private String lastSiret;

    public List<RneCompany> getCompanies() {
        return companies;
    }

    public void setCompanies(List<RneCompany> companies) {
        this.companies = companies;
    }

    public String getLastSiret() {
        return lastSiret;
    }

    public void setLastSiret(String lastSiret) {
        this.lastSiret = lastSiret;
    }

}
