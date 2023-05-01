package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Appoint;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.repository.AppointRepository;

@Service
public class AppointServiceImpl implements AppointService {

    @Autowired
    AppointRepository appointRepository;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Override
    public List<Appoint> getAppoints(String searchLabel) {
        return appointRepository.findByLabelContainingIgnoreCase(searchLabel);
    }

    @Override
    public Appoint getAppoint(Integer id) {
        Optional<Appoint> appoint = appointRepository.findById(id);
        if (appoint.isPresent())
            return appoint.get();
        return null;
    }

    private Appoint addOrUpdateAppoint(Appoint appoint) {
        return appointRepository.save(appoint);
    }

    @Override
    public Appoint generateAppointForInvoice(Invoice invoice, Payment originPayment, Deposit deposit,
            Float appointAmount)
            throws OsirisException {
        Appoint appoint = new Appoint();
        appoint.setAppointAmount(appointAmount);
        appoint.setLabel(
                "Appoint pour la facture n°" + invoice.getId() + " sur le paiement n°" + originPayment.getId());
        appoint.setOriginPayment(originPayment);
        appoint.setInvoice(invoice);
        appoint.setAppointDate(LocalDate.now());
        addOrUpdateAppoint(appoint);

        if (deposit == null)
            accountingRecordService.generateAppointForPayment(originPayment, appointAmount,
                    invoiceHelper.getCustomerOrder(invoice), appoint, invoice);
        else
            accountingRecordService.generateAppointForDeposit(deposit, appointAmount,
                    invoiceHelper.getCustomerOrder(invoice), appoint, invoice);

        return appoint;
    }
}
