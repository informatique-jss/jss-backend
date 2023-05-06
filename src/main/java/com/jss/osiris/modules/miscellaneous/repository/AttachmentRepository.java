package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import com.jss.osiris.libs.QueryCacheCrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.miscellaneous.model.Attachment;

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
}