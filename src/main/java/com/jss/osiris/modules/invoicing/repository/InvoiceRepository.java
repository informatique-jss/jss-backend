package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

    Invoice findByCustomerOrderId(Integer customerOrderId);

    @Query("select min(createdDate) from Invoice where  tiers=:tiers")
    LocalDate findFirstBillingDateForTiers(@Param("tiers") Tiers tiers);

    @Query("select min(createdDate) from Invoice where  responsable=:responsable")
    LocalDate findFirstBillingDateForResponsable(@Param("responsable") Responsable responsable);

    @Query("select i from Invoice i left join fetch i.tiers t left join fetch i.responsable r left join fetch i.confrere c left join fetch i.customerOrder cu   where i.invoiceStatus IN :invoiceStatus and i.createdDate>=:startDate and i.createdDate<=:endDate")
    List<Invoice> findInvoice(@Param("invoiceStatus") List<InvoiceStatus> invoiceStatus,
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}