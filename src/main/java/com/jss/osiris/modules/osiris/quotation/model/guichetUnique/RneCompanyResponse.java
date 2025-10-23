package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class RneCompanyResponse {
    private RneCompany company;

    public RneCompany getCompany() {
        return company;
    }

    public void setCompany(RneCompany company) {
        this.company = company;
    }

}
