package com.jss.osiris.modules.osiris.invoicing.model;

import java.io.Serializable;

import jakarta.persistence.Column;

public class CompositeInpiInvoicingEntityKey implements Serializable {

    private Integer inpiOrder;

    @Column(nullable = false)
    private boolean isCreditNote;

    public Integer getInpiOrder() {
        return inpiOrder;
    }

    public void setInpiOrder(Integer inpiOrder) {
        this.inpiOrder = inpiOrder;
    }

    public boolean isCreditNote() {
        return isCreditNote;
    }

    public void setCreditNote(boolean isCreditNote) {
        this.isCreditNote = isCreditNote;
    }

}
