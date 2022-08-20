package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

    Invoice findByCustomerOrderId(Integer customerOrderId);

    @Query("select min(createdDate) from Invoice where  tiers=:tiers")
    LocalDate findFirstBillingDateForTiers(@Param("tiers") Tiers tiers);

    @Query("select min(createdDate) from Invoice where  responsable=:responsable")
    LocalDate findFirstBillingDateForResponsable(@Param("responsable") Responsable responsable);
}