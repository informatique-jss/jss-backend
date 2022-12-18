package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.miscellaneous.model.Attachment;

public interface AttachmentRepository extends CrudRepository<Attachment, Integer> {
    @Query(value = "select a from Attachment a where id_tiers =:idTiers")
    List<Attachment> findByTiersId(@Param("idTiers") Integer idTiers);

    @Query(value = "select a from Attachment a where id_responsable =:idResponsable")
    List<Attachment> findByResponsableId(@Param("idResponsable") Integer idResponsable);

    @Query(value = "select a from Attachment a where id_quotation =:idQuotation")
    List<Attachment> findByQuotationId(@Param("idQuotation") Integer idQuotation);

    @Query(value = "select a from Attachment a where id_domiciliation =:idDomiciliation")
    List<Attachment> findByDomiciliationId(@Param("idDomiciliation") Integer idDomiciliation);

    @Query(value = "select a from Attachment a where id_announcement =:idAnnouncement")
    List<Attachment> findByAnnouncementId(@Param("idAnnouncement") Integer idAnnouncement);

    @Query(value = "select a from Formalite a where id_formalite =:idFormalite")
    List<Attachment> findByFormaliteId(@Param("idFormalite") Integer idFormalite);

    @Query(value = "select a from Attachment a where id_bodacc =:idBodacc")
    List<Attachment> findByBodaccId(@Param("idBodacc") Integer idBodacc);

    @Query(value = "select a from Attachment a where id_provision =:idProvision")
    List<Attachment> findByProvisionId(@Param("idProvision") Integer idProvision);

    @Query(value = "select a from Attachment a where id_customer_order =:idCustomerOrder")
    List<Attachment> findByCustomerOrderId(@Param("idCustomerOrder") Integer idCustomerOrder);

    @Query(value = "select a from Attachment a where id_journal =:idJournal")
    List<Attachment> findByCustomerJournalId(@Param("idJournal") Integer idJournal);

    @Query(value = "select a from Attachment a where id_simple_provision =:idSimpleProvision")
    List<Attachment> findBySimpleProvisonId(@Param("idSimpleProvision") Integer idSimpleProvision);

    @Query(value = "select a from Attachment a where id_invoice =:idInvoice")
    List<Attachment> findByInvoiceId(@Param("idInvoice") Integer idSimpleProvision);
}