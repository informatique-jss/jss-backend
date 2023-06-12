package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;

public interface AttachmentRepository extends QueryCacheCrudRepository<Attachment, Integer> {
        @Query(value = "select a from Attachment a where id_tiers =:idTiers")
        List<Attachment> findByTiersId(@Param("idTiers") Integer idTiers);

        @Query(value = "select a from Attachment a where id_responsable =:idResponsable")
        List<Attachment> findByResponsableId(@Param("idResponsable") Integer idResponsable);

        @Query(value = "select a from Attachment a where id_quotation =:idQuotation")
        List<Attachment> findByQuotationId(@Param("idQuotation") Integer idQuotation);

        @Query(value = "select a from Attachment a where id_provision =:idProvision")
        List<Attachment> findByProvisionId(@Param("idProvision") Integer idProvision);

        @Query(value = "select a from Attachment a where id_customer_order =:idCustomerOrder")
        List<Attachment> findByCustomerOrderId(@Param("idCustomerOrder") Integer idCustomerOrder);

        @Query(value = "select a from Attachment a where id_invoice =:idInvoice")
        List<Attachment> findByInvoiceId(@Param("idInvoice") Integer idInvoice);

        @Query(value = "select a from Attachment a where id_customer_mail =:idCustomerMail")
        List<Attachment> findByCustomerMailId(@Param("idCustomerMail") Integer idCustomerMail);

        @Query(value = "select a from Attachment a where id_provider =:idProvider")
        List<Attachment> findByProviderId(@Param("idProvider") Integer idProvider);

        @Query(value = "select a from Attachment a where id_competent_authority =:idCompetentAuthority")
        List<Attachment> findByCompetentAuthorityId(@Param("idCompetentAuthority") Integer idCompetentAuthority);

        @Query(nativeQuery = true, value = " " +
                        " select distinct a.* " +
                        " from attachment a " +
                        " join provision p on p.id = a.id_provision " +
                        " join asso_affaire_order asso on asso.id = p.id_asso_affaire_order " +
                        " join customer_order c on c.id = asso.id_customer_order " +
                        " where a.is_disabled=false and lower(a.description) like '%.pdf' and a.id_attachment_type =:attachmentTypeInvoiceId and a.id_azure_invoice is  null  and c.id_customer_order_status not in (:customerOrderStatusExcluded)  limit 10 ")
        List<Attachment> findInvoiceAttachmentOnProvisionToAnalyse(
                        @Param("attachmentTypeInvoiceId") Integer attachmentTypeInvoiceId,
                        @Param("customerOrderStatusExcluded") List<CustomerOrderStatus> customerOrderStatusExcluded);

        @Query(nativeQuery = true, value = " " +
                        " select distinct a.* " +
                        " from attachment a " +
                        " where a.is_disabled=false and  a.id_attachment_type =:attachmentTypeBillingClosureId and a.id_azure_receipt is  null  and id_competent_authority is not null limit 10 ")
        List<Attachment> findReceiptAttachmentOnCompetentAuthorityToAnalyse(
                        @Param("attachmentTypeBillingClosureId") Integer attachmentTypeBillingClosureId);
}