package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.invoicing.model.InfogreffeInvoice;

public interface InfogreffeInvoiceRepository extends CrudRepository<InfogreffeInvoice, Integer> {

    InfogreffeInvoice findByInvoiceDateTimeAndSirenAffaireAndCustomerReference(LocalDateTime invoiceDateTime,
            String sirenAffaire, String customerReference);

    List<InfogreffeInvoice> findByDebourIsNull();

    List<InfogreffeInvoice> findByCustomerReferenceContainingIgnoreCaseAndDebourIsNull(String customerReference);
}