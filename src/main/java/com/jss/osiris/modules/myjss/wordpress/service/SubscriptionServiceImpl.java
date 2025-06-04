package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription.SubscriptionTypeEnum;
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
    public Subscription givePostSubscription(Post postToOffer, Mail recipientMail) {
        Responsable signedInUser = employeeService.getCurrentMyJssUser();

        boolean canSharePost = false;

        // Verify that connceted user can offer the post
        if (signedInUser != null) {
            if (signedInUser.getMail() != null) {
                List<Subscription> subscriptions = getSubscriptionsForMail(signedInUser.getMail());
                for (Subscription sub : subscriptions) {
                    if (sub.getStartDate() != null && sub.getEndDate() != null
                            && sub.getStartDate().isBefore(LocalDate.now())
                            && LocalDate.now().isBefore(sub.getEndDate())) {

                        if (signedInUser.getNumberOfPostsSharingAuthorized() == null) {
                            // TODO : create constant 5 ?
                            signedInUser.setNumberOfPostsSharingAuthorized(5);
                        }

                        // Get number of post shared and compare with max share possible
                        if (signedInUser
                                .getNumberOfPostsSharingAuthorized() > getNumberOfPostSharedOnMonth(recipientMail)) {
                            canSharePost = true;
                        }
                    }
                }
            }
        }

        if (canSharePost) {
            Subscription onePostSubscriptionGiven = new Subscription();
            onePostSubscriptionGiven.setSubscriptionType(SubscriptionTypeEnum.SHARED_POST_SUBSCRIPTION);
            onePostSubscriptionGiven.setPost(postToOffer);
            onePostSubscriptionGiven.setSharedDate(LocalDate.now());
            onePostSubscriptionGiven.setSubscriptionOfferedMail(recipientMail);
            onePostSubscriptionGiven.setSubcriptionMail(signedInUser.getMail());
            onePostSubscriptionGiven.setValidationToken(UUID.randomUUID().toString());

            return subscriptionRepository.save(onePostSubscriptionGiven);
        }

        return null;
    }

    @Override
    public Integer getNumberOfPostSharedOnMonth(Mail responsableMail) {
        subscriptionRepository.getNumberOfPostSharedByResponsableFromDateAndSubscriptionType(responsableMail,
                LocalDate.now().withDayOfMonth(1), SubscriptionTypeEnum.SHARED_POST_SUBSCRIPTION);
        return null;
    }
}
