package com.jss.osiris.modules.osiris.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.QueryHint;

public interface InvoiceRepository extends QueryCacheCrudRepository<Invoice, Integer> {

        List<Invoice> findByCustomerOrderId(Integer customerOrderId);

        @Query(nativeQuery = true, value = "select "
                        + " i.id as invoiceId,"
                        + " ist.label as invoiceStatus,"
                        + " ist.code as invoiceStatusCode,"
                        + " ist.id as invoiceStatusId,"
                        + " c.id as customerOrderId,"
                        + "  r1.firstname || ' '||r1.lastname as customerOrderLabel,"
                        + " pro.label as providerLabel, "
                        + " r1.id as responsableId, "
                        + " coalesce(r1.id_commercial, t.id_commercial) as salesEmployeeId, "
                        + " t.id as tiersId, "
                        + " r1.firstname || ' '||r1.lastname  as responsableLabel,"
                        + " coalesce( t.denomination,t.firstname || ' '||t.lastname )  as tiersLabel,"
                        + " i.id_payment_type as idPaymentType,"
                        + " STRING_AGG( case when af.denomination is not null and af.denomination!='' then af.denomination else af.firstname || ' '||af.lastname end || ' ('||city.label ||')',', ' order by 1) as affaireLabel,"
                        + "  i.billing_label as billingLabel,"
                        + "  i.created_date as createdDate,"
                        + "  i.total_price as totalPrice,"
                        + "  c.description as customerOrderDescription,"
                        + "  i.first_reminder_date_time as firstReminderDateTime,"
                        + "  i.second_reminder_date_time as secondReminderDateTime,"
                        + "  i.third_reminder_date_time as thirdReminderDateTime,"
                        + "  i.manual_accounting_document_number as manualAccountingDocumentNumber,"
                        + "  i.manual_accounting_document_date as manualAccountingDocumentDate,"
                        + "  i.due_date as dueDate,"
                        + "  max(follow.followup_date) as lastFollowupDate,"
                        + "  COALESCE(i.total_price,0) - sum(COALESCE(case when p.is_appoint then -1 else 1 end * p.payment_amount,0)) as remainingToPay,"
                        + "  case when invoicing_document.is_recipient_affaire then 'Affaire' else 'Donneur d''ordre' end as invoiceRecipient,"
                        + "  bt.label as invoiceBillingType,"
                        + "  STRING_AGG( cast(p.id as text),', ' order by 1) as paymentId"
                        + " from invoice i"
                        + " join invoice_status ist on ist.id = i.id_invoice_status "
                        + " left join customer_order c on c.id = i.customer_order_id"
                        + " left join asso_affaire_order asso on asso.id_customer_order = c.id"
                        + " left join affaire af on af.id = asso.id_affaire"
                        + " left join city on af.id_city = city.id"
                        + " left join responsable r1 on r1.id = c.id_responsable"
                        + " left join tiers t on r1.id_tiers =t.id "
                        + " left join provider pro on pro.id = i.id_provider"
                        + " left join payment p on p.id_invoice = i.id and p.is_cancelled = false"
                        + " left join tiers_followup follow on follow.id_invoice = i.id"
                        + " left join document invoicing_document on invoicing_document.id_customer_order= c.id and invoicing_document.id_document_type = :invoicingDocumentTypeId "
                        + " left join billing_label_type bt on bt.id = i.id_billing_label_type "
                        + " where i.created_date>=:startDate and i.created_date<=:endDate "
                        + " and  ( COALESCE(:invoiceStatus)=0 or ist.id in (:invoiceStatus)) "
                        + " and  ( COALESCE(:customerOrderId)=0 or c.id in (:customerOrderId)) "
                        + " and  ( COALESCE(:customerOrderForInboundInvoiceId)=0 or i.id_customer_order_for_inbound_invoice in (:customerOrderForInboundInvoiceId)) "
                        + " and  ( COALESCE(:invoiceId)=0 or i.id in (:invoiceId)) "
                        + " and  ( COALESCE(:customerOrderIds) =0 or t.id in (:customerOrderIds) or r1.id in (:customerOrderIds) or pro.id in (:customerOrderIds)  ) "
                        + " and (:minAmount is null or total_price>=CAST(CAST(:minAmount as text) as numeric(15, 2)) ) "
                        + " and  ( COALESCE(:salesEmployeeId) =0 or t.id_commercial=:salesEmployeeId or r1.id_commercial=:salesEmployeeId ) "
                        + " and (:maxAmount is null or total_price<=CAST(CAST(:maxAmount as text) as numeric(15, 2)) )"
                        + " and (:showToRecover is false or (  i.first_reminder_date_time is not null and  i.second_reminder_date_time  is not null and  i.third_reminder_date_time  is not null and i.id_invoice_status<>:invoicePayedStatusId ) )"
                        + " group by i.id, bt.label, ist.label,ist.code,ist.id, pro.label, c.id,  r1.id, r1.firstname,t.id, r1.lastname,"
                        + " t.denomination, t.firstname, t.lastname, r1.firstname, r1.lastname, i.billing_label, i.created_date, i.total_price,"
                        + " i.first_reminder_date_time , i.second_reminder_date_time,i.third_reminder_date_time, invoicing_document.is_recipient_affaire, i.due_date ,c.description")
        List<InvoiceSearchResult> findInvoice(@Param("invoiceStatus") List<Integer> invoiceStatus,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                        @Param("minAmount") Float minAmount, @Param("maxAmount") Float maxAmount,
                        @Param("showToRecover") Boolean showToRecover,
                        @Param("invoicePayedStatusId") Integer invoicePayedStatusId,
                        @Param("invoiceId") Integer invoiceId,
                        @Param("customerOrderId") Integer customerOrderId,
                        @Param("customerOrderIds") List<Integer> customerOrderIds,
                        @Param("salesEmployeeId") Integer salesEmployeeId,
                        @Param("customerOrderForInboundInvoiceId") Integer customerOrderForInboundInvoiceId,
                        @Param("invoicingDocumentTypeId") Integer invoicingDocumentTypeId);

        @Query(value = "select n from Invoice n where invoiceStatus=:invoiceStatus and thirdReminderDateTime is null ")
        List<Invoice> findInvoiceForReminder(@Param("invoiceStatus") InvoiceStatus invoiceStatus);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Invoice> findByCustomerOrderForInboundInvoiceId(Integer customerOrderId);

        List<Invoice> findByProviderAndManualAccountingDocumentNumberIgnoreCase(Provider provider,
                        String manualDocumentNumber);

        List<Invoice> findByProviderAndManualAccountingDocumentNumberContainingIgnoreCase(
                        Provider provider, String manualDocumentNumber);

        @Query(value = "select i.* from invoice i where id_direct_debit_transfert=:id", nativeQuery = true)
        Invoice searchInvoicesByIdDirectDebitTransfert(@Param("id") Integer idToFind);

        @Query(value = "select n from Invoice n where invoiceStatus=:invoiceStatus and thirdReminderDateTime is null and billingLabelType=:billingLabelType   ")
        List<Invoice> findInvoiceForCustomReminder(@Param("invoiceStatus") InvoiceStatus invoiceStatusSend,
                        @Param("billingLabelType") BillingLabelType billingLabelType);

        @Query("select i from Invoice i where responsable in :responsables and (invoiceStatus in :invoiceStatus)")
        List<Invoice> searchInvoices(List<Responsable> responsables, List<InvoiceStatus> invoiceStatus);

        @Modifying
        @Query(nativeQuery = true, value = " delete from invoice where  id  in (select id from reprise_inpi_del)")
        void deleteDuplicateInvoices();

}