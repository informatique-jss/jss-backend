package com.jss.osiris.modules.invoicing.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.invoicing.model.OwncloudGreffeInvoice;
import com.jss.osiris.modules.invoicing.repository.OwncloudGreffeInvoiceRepository;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Service
public class OwncloudGreffeInvoiceServiceImpl implements OwncloudGreffeInvoiceService {

    @Autowired
    OwncloudGreffeInvoiceRepository owncloudGreffeInvoiceRepository;

    @Override
    public OwncloudGreffeInvoice getOwncloudGreffeInvoice(Integer id) {
        Optional<OwncloudGreffeInvoice> owncloudGreffeInvoice = owncloudGreffeInvoiceRepository.findById(id);
        if (owncloudGreffeInvoice.isPresent())
            return owncloudGreffeInvoice.get();
        return null;
    }

    @Override
    public OwncloudGreffeInvoice addOrUpdateOwncloudGreffeInvoice(OwncloudGreffeInvoice owncloudGreffeInvoice) {
        return owncloudGreffeInvoiceRepository.save(owncloudGreffeInvoice);
    }

    @Override
    public List<OwncloudGreffeInvoice> getCorrespondingGreffeInvoiceForCustomerOrder(CustomerOrder customerOrder) {
        return owncloudGreffeInvoiceRepository.findCorrespondingGreffeInvoiceForCustomerOrder(customerOrder);
    }

    @Override
    public List<OwncloudGreffeInvoice> getOwncloudGreffeInvoiceByNumero(String numero) {
        return owncloudGreffeInvoiceRepository.findCorrespondingGreffeInvoiceForNumero(numero);
    }
}
