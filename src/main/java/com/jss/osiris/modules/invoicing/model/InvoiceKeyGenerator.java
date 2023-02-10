package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.math.BigInteger;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.spi.NativeQueryImplementor;

public class InvoiceKeyGenerator implements IdentifierGenerator {

    @SuppressWarnings({ "all" })
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Invoice invoice = (Invoice) obj;
        String seqName = "";
        if (invoice.getIsInvoiceFromProvider()) {
            seqName = "hibernate_sequence";
        } else {
            seqName = "invoice_sequence";
        }
        NativeQueryImplementor query = session.createNativeQuery("SELECT nextval('" + seqName + "')");
        return ((BigInteger) query.getSingleResult()).intValue();
    }
}