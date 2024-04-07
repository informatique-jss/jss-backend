package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.math.BigInteger;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class InvoiceKeyGenerator implements IdentifierGenerator {

    @SuppressWarnings({ "all" })
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Invoice invoice = (Invoice) obj;
        String seqName = "";
        if (invoice.getIsInvoiceFromProvider() != null && invoice.getIsInvoiceFromProvider()
                || invoice.getIsProviderCreditNote() != null && invoice.getIsProviderCreditNote()) {
            seqName = "hibernate_sequence";
        } else {
            seqName = "invoice_sequence";
        }
        return ((BigInteger) session.createNativeQuery("SELECT nextval('" + seqName + "')").getSingleResult())
                .intValue();
    }
}