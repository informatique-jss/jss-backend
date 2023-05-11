package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.invoicing.model.InfogreffeInvoice;

public interface InfogreffeInvoiceRepository extends QueryCacheCrudRepository<InfogreffeInvoice, Integer> {

    InfogreffeInvoice findByInvoiceDateTimeAndSirenAffaireAndCustomerReference(LocalDateTime invoiceDateTime,
            String sirenAffaire, String customerReference);

    List<InfogreffeInvoice> findByDebourIsNull();

    List<InfogreffeInvoice> findByCustomerReferenceContainingIgnoreCaseAndDebourIsNull(String customerReference);
}