package com.jss.osiris.libs.mail.repository;

import java.util.List;

import javax.persistence.QueryHint;

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

public interface CustomerMailRepository extends QueryCacheCrudRepository<CustomerMail, Integer> {

    @Query("select m from CustomerMail m where hasErrors=false and isSent = false order by createdDateTime asc")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<CustomerMail> findAllByOrderByCreatedDateTimeAsc();

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

    @Query(nativeQuery = true, value = "" +
            " select cm.* " +
            " from customer_mail cm  " +
            " where cm.id_responsable =:idResponsable " +
            " and cm.created_date_time >=date_trunc('month',now()) " +
            " and cm.subject ='Votre relevé de compte' ")
    List<CustomerMail> findReceiptMailsForResponsable(@Param("idResponsable") Integer idResponsable);

    @Query(nativeQuery = true, value = "" +
            " select cm.* " +
            " from customer_mail cm  " +
            " where cm.id_tiers =:idTiers " +
            " and cm.created_date_time >=date_trunc('month',now()) " +
            " and cm.subject ='Votre relevé de compte' ")
    List<CustomerMail> findReceiptMailsForTiers(@Param("idTiers") Integer idTiers);
}