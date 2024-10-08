package com.jss.osiris.modules.osiris.invoicing.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;

public interface InvoiceItemRepository extends QueryCacheCrudRepository<InvoiceItem, Integer> {
}