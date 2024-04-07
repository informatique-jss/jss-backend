package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;

public interface AttachmentRepository extends QueryCacheCrudRepository<Attachment, Integer> {
        @Query(value = "select a.* from attachment a where id_tiers =:idTiers", nativeQuery = true)
        List<Attachment> findByTiersId(@Param("idTiers") Integer idTiers);

        @Query(value = "select a.* from attachment a where id_responsable =:idResponsable", nativeQuery = true)
        List<Attachment> findByResponsableId(@Param("idResponsable") Integer idResponsable);

        @Query(value = "select a.* from attachment a where id_quotation =:idQuotation", nativeQuery = true)
        List<Attachment> findByQuotationId(@Param("idQuotation") Integer idQuotation);

        @Query(value = "select a.* from attachment a where id_provision =:idProvision", nativeQuery = true)
        List<Attachment> findByProvisionId(@Param("idProvision") Integer idProvision);

        @Query(value = "select a.* from attachment a where id_customer_order =:idCustomerOrder", nativeQuery = true)
        List<Attachment> findByCustomerOrderId(@Param("idCustomerOrder") Integer idCustomerOrder);

        @Query(value = "select a.* from attachment a where id_invoice =:idInvoice", nativeQuery = true)
        List<Attachment> findByInvoiceId(@Param("idInvoice") Integer idInvoice);

        @Query(value = "select a.* from attachment a where id_affaire =:idAffaire", nativeQuery = true)
        List<Attachment> findByAffaireId(@Param("idAffaire") Integer idAffaire);

        @Query(value = "select a.* from attachment a where id_customer_mail =:idCustomerMail", nativeQuery = true)
        List<Attachment> findByCustomerMailId(@Param("idCustomerMail") Integer idCustomerMail);

        @Query(value = "select a.* from attachment a where id_provider =:idProvider", nativeQuery = true)
        List<Attachment> findByProviderId(@Param("idProvider") Integer idProvider);

        @Query(value = "select a.* from attachment a where id_competent_authority =:idCompetentAuthority", nativeQuery = true)
        List<Attachment> findByCompetentAuthorityId(@Param("idCompetentAuthority") Integer idCompetentAuthority);

        @Query(value = "select a.* from attachment a where id_asso_service_document =:idAssoServiceDocument", nativeQuery = true)
        List<Attachment> findByAssoServiceDocument(@Param("idAssoServiceDocument") Integer idAssoServiceDocument);
}