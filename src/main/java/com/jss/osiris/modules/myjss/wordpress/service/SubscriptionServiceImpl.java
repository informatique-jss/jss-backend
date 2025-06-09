package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.repository.SubscriptionRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.transaction.Transactional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MailHelper mailHelper;

    @Override
    public List<Subscription> getSubscriptionsForMail(Mail mail) {
        return subscriptionRepository.findBySubcriptionMail(mail);
    }

    @Override
    public Subscription getSubscriptionByToken(String token) {
        return subscriptionRepository.findByValidationToken(token);
    }

    @Override
    @Transactional
    public Subscription addOrUpdateSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription givePostSubscription(Post postToOffer, Mail recipientMail) throws OsirisException {
        Responsable signedInUser = employeeService.getCurrentMyJssUser();

        Integer remainingPostsToShare = getRemainingPostToShareForCurrentMonth(signedInUser);

        if (remainingPostsToShare == null)
            return null;

        if (remainingPostsToShare > 0) {
            Subscription onePostSubscriptionGiven = new Subscription();
            onePostSubscriptionGiven.setSubscriptionType(Subscription.SHARED_POST_SUBSCRIPTION);
            onePostSubscriptionGiven.setPost(postToOffer);
            onePostSubscriptionGiven.setSharedDate(LocalDate.now());
            onePostSubscriptionGiven.setSubscriptionOfferedMail(recipientMail);
            onePostSubscriptionGiven.setSubcriptionMail(signedInUser.getMail());
            onePostSubscriptionGiven.setValidationToken(UUID.randomUUID().toString());

            Subscription newSubscription = subscriptionRepository.save(onePostSubscriptionGiven);

            mailHelper.sendGiftedPost(newSubscription);
            return newSubscription;
        }

        return null;
    }

    /**
     * @return null ==> no annual subscription ==> impossible to share posts
     */
    @Override
    public Integer getRemainingPostToShareForCurrentMonth(Responsable signedInUser) {
        if (signedInUser.getMail() != null && signedInUser.getNumberOfGiftPostsPerMonth() != null
                && signedInUser.getNumberOfGiftPostsPerMonth() > 0) {
            // Get number of remaining posts to share
            return signedInUser.getNumberOfGiftPostsPerMonth()
                    - getNumberOfPostSharedForCurrentMonthForUser(
                            signedInUser.getMail());
        }
        return null;
    }

    private Integer getNumberOfPostSharedForCurrentMonthForUser(Mail responsableMail) {
        return subscriptionRepository.getNumberOfPostSharedByResponsableFromDateAndSubscriptionType(
                responsableMail.getId(), LocalDate.now().withDayOfMonth(1),
                Subscription.SHARED_POST_SUBSCRIPTION);
    }

}
