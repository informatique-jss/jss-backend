package com.jss.osiris.modules.osiris.invoicing.repository;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.InpiInvoicingExtract;

public interface InpiInvoicingExtractRepository extends QueryCacheCrudRepository<InpiInvoicingExtract, Integer> {
    List<InpiInvoicingExtract> findByAccountingDateBetween(LocalDate startDate, LocalDate endDate);
}