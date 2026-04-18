package com.jss.osiris.modules.osiris.invoicing.model;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class InvoiceKeyGenerator implements IdentifierGenerator {

    @SuppressWarnings({ "all" })
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Invoice invoice = (Invoice) obj;
        String seqName = "";

        if (invoice.getId() != null && invoice.getId() > 0) {
            return invoice.getId();
        }
        return ((Long) session.createNativeQuery("SELECT nextval('" + seqName + "')").getSingleResult())
                .intValue();
    }
}