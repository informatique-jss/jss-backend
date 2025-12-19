package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuichetUniqueTransfertAgent {
    private Integer newOwnerId;

    public Integer getNewOwnerId() {
        return newOwnerId;
    }

    public void setNewOwnerId(Integer newOwnerId) {
        this.newOwnerId = newOwnerId;
    }

}
