package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription.SubscriptionTypeEnum;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface SubscriptionRepository extends QueryCacheCrudRepository<Subscription, Integer> {

    List<Subscription> findBySubcriptionMail(Mail mail);

    Subscription findByValidationToken(String validationToken);

    @Query("SELECT COUNT(s) "
            + "FROM Subscription s WHERE s.subcriptionMail = :responsableMail "
            + "AND s.subscriptionType = :subscriptionType "
            + "AND s.sharedDate BETWEEN :startOfMonth AND CURRENT_DATE")
    Integer getNumberOfPostSharedByResponsableFromDateAndSubscriptionType(Mail responsableMail, LocalDate startOfMonth,
            SubscriptionTypeEnum subscriptionType);
}