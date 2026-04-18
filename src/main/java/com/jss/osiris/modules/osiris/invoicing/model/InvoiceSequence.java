package com.jss.osiris.modules.osiris.invoicing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "INVOICE_SEQUENCE_CUSTOMER")
public class InvoiceSequence {
    @Id
    @SequenceGenerator(name = "invoice_sequence_sequence", sequenceName = "invoice_sequence_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_sequence_sequence")
    private Integer id;

    private Integer lastValue;

    public Integer getLastValue() {
        return lastValue;
    }

    public void setLastValue(Integer lastValue) {
        this.lastValue = lastValue;
    }

}