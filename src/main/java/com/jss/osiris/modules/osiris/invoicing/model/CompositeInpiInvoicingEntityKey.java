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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((inpiOrder == null) ? 0 : inpiOrder.hashCode());
        result = prime * result + (isCreditNote ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompositeInpiInvoicingEntityKey other = (CompositeInpiInvoicingEntityKey) obj;
        if (inpiOrder == null) {
            if (other.inpiOrder != null)
                return false;
        } else if (!inpiOrder.equals(other.inpiOrder))
            return false;
        if (isCreditNote != other.isCreditNote)
            return false;
        return true;
    }

}
