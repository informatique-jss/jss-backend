package com.jss.osiris.libs.mail.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

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
}