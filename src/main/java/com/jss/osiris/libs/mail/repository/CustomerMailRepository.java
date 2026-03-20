package com.jss.osiris.libs.mail.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.persistence.QueryHint;

public interface CustomerMailRepository extends QueryCacheCrudRepository<CustomerMail, Integer> {

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CustomerMail> findByQuotation(Quotation quotation);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CustomerMail> findByCustomerOrder(CustomerOrder customerOrder);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CustomerMail> findByConfrere(Confrere confrere);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CustomerMail> findByTiers(Tiers tiers);

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<CustomerMail> findByResponsable(Responsable responsable);

        @Query("select m from CustomerMail m where isCancelled=false and isSent = false and toSendAfter<:dateToSend  ")
        List<CustomerMail> findTemporizesMailsToSend(@Param("dateToSend") LocalDateTime dateToSend);

        @Query("select m from CustomerMail m where isCancelled=false and isSent = false and toSendAfter is not null and customerOrder=:customerOrder ")
        List<CustomerMail> findTemporizedMailsForCustomerOrder(@Param("customerOrder") CustomerOrder customerOrder);

        @Modifying
        @Query(value = "update customer_mail set id_provision = null where id_provision = :idProvision", nativeQuery = true)
        void nullifyProvisionId(@Param("idProvision") Integer idProvision);

        @Modifying
        @Query(value = "update customer_mail set id_missing_attachment_query = null where id_missing_attachment_query in (select id from missing_attachment_query where id_service = :idService )", nativeQuery = true)
        void nullifyIdMissingAttachementQuery(@Param("idService") Integer idService);

}