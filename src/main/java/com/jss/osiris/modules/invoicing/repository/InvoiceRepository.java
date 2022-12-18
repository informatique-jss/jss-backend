package com.jss.osiris.modules.invoicing.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

        Invoice findByCustomerOrderId(Integer customerOrderId);

        @Query("select min(createdDate) from Invoice where  tiers=:tiers")
        LocalDate findFirstBillingDateForTiers(@Param("tiers") Tiers tiers);

        @Query("select min(createdDate) from Invoice where  responsable=:responsable")
        LocalDate findFirstBillingDateForResponsable(@Param("responsable") Responsable responsable);

        @Query(nativeQuery = true, value = "select "
                        + " i.id as invoiceId,"
                        + " ist.label as invoiceStatus,"
                        + " ist.id as invoiceStatusId,"
                        + " c.id as customerOrderId,"
                        + " case when co.id is not null then co.label"
                        + " when r1.id is not null then  r1.firstname || ' '||r1.lastname "
                        + " else case when t.denomination is not null and t.denomination!='' then t.denomination else t.firstname || ' '||t.lastname end end as customerOrderLabel,"
                        + " co.id as confrereId, "
                        + " r1.id as responsableId, "
                        + " t.id as tiersId, "
                        + " r1.firstname || ' '||r1.lastname  as responsableLable,"
                        + " STRING_AGG( case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end,', ' order by 1) as affaireLabel,"
                        + "  i.billing_label as billingLabel,"
                        + "  i.created_date as createdDate,"
                        + "  i.total_price as totalPrice,"
                        + "  c.description as customerOrderDescription,"
                        + "  STRING_AGG( cast(p.id as text),', ' order by 1) as paymentId"
                        + " from invoice i"
                        + " join invoice_status ist on ist.id = i.id_invoice_status "
                        + " left join customer_order c on c.id = i.customer_order_id"
                        + " left join asso_affaire_order asso on asso.id_customer_order = c.id"
                        + " left join affaire af on af.id = asso.id_affaire"
                        + " left join tiers t on t.id = c.id_tiers"
                        + " left join responsable r1 on r1.id = c.id_responsable"
                        + " left join confrere co on co.id = c.id_confrere"
                        + " left join payment p on p.id_invoice = i.id"
                        + " where i.created_date>=:startDate and i.created_date<=:endDate "
                        + " and  ( COALESCE(:invoiceStatus) is null or ist.id in (:invoiceStatus)) "
                        + " and (:minAmount is null or total_price>=CAST(CAST(:minAmount as text) as real) ) "
                        + " and (:maxAmount is null or total_price<=CAST(CAST(:maxAmount as text) as real) )"
                        + " group by i.id, ist.label,ist.id, c.id, co.id, co.label, r1.id, r1.firstname,t.id, r1.lastname,"
                        + " t.denomination, t.firstname, t.lastname, r1.firstname, r1.lastname, i.billing_label, i.created_date, i.total_price, c.description")
        List<InvoiceSearchResult> findInvoice(@Param("invoiceStatus") List<Integer> invoiceStatus,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount);

        @Query(value = "select n from Invoice n where invoiceStatus=:invoiceStatus and thirdReminderDateTime is null ")
        List<Invoice> findInvoiceForReminder(@Param("invoiceStatus") InvoiceStatus invoiceStatus);

}