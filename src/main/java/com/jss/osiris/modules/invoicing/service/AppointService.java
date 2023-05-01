package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Appoint;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;

public interface AppointService {
    public List<Appoint> getAppoints(String searchLabel);

    public Appoint getAppoint(Integer id);

    public Appoint generateAppointForInvoice(Invoice invoice, Payment originPayment, Deposit deposit,
            Float appointAmount)
            throws OsirisException;

}
