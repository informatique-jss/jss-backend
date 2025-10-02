package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.myjss.wordpress.model.AssoProvisionPostNewspaper;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.repository.SubscriptionRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.transaction.Transactional;

@org.springframework.stereotype.Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MailHelper mailHelper;

    @Autowired
    ConstantService constantService;

    @Autowired
    AssoProvisionPostNewspaperService assoProvisionPostNewspaperService;

    @Autowired
    ResponsableService responsableService;

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
     * @return null ==> no annual/monthly subscription ==> impossible to share posts
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

    @Override
    public boolean canResponsableSeeKioskNewspaper(Responsable responsable, Newspaper newspaper) {
        List<Subscription> subscriptions = getSubscriptionsForMail(responsable.getMail());

        Boolean canSeeNewspaper = false;

        canSeeNewspaper = isResponsableHasFullValidSubscription(responsable, subscriptions);

        if (!canSeeNewspaper) {
            for (Subscription subscription : subscriptions) {
                switch (subscription.getSubscriptionType()) {
                    case Subscription.NEWSPAPER_KIOSK_BUY:
                        if (subscription.getNewspaper().getId().equals(newspaper.getId()))
                            canSeeNewspaper = true;
                        break;
                }
                if (canSeeNewspaper)
                    break;
            }
        }

        return canSeeNewspaper;
    }

    @Override
    public boolean isResponsableHasFullValidSubscription(Responsable responsable, List<Subscription> subscriptions) {

        Boolean hasFullSubscriptionValid = false;

        for (Subscription subscription : subscriptions) {
            if (subscription.getSubscriptionType().equals(Subscription.ANNUAL_SUBSCRIPTION)
                    || subscription.getSubscriptionType().equals(Subscription.ENTERPRISE_ANNUAL_SUBSCRIPTION)
                    || subscription.getSubscriptionType().equals(Subscription.MONTHLY_SUBSCRIPTION)) {
                if (subscription.getStartDate().isBefore(LocalDate.now())
                        && LocalDate.now().isBefore(subscription.getEndDate())
                        || subscription.getStartDate().equals(LocalDate.now())
                        || subscription.getEndDate().equals(LocalDate.now()))
                    hasFullSubscriptionValid = true;
            }
            if (hasFullSubscriptionValid)
                break;
        }
        return hasFullSubscriptionValid;
    }

    @Override
    public void saveSubscription(CustomerOrder customerOrder) throws OsirisException {
        for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
            for (Service service : asso.getServices()) {
                if (isJssSubscriptionService(service)) {
                    Subscription newSubscription = new Subscription();
                    newSubscription.setSubcriptionMail(service.getAssoAffaireOrder()
                            .getCustomerOrder().getResponsable().getMail());

                    if (service.getServiceTypes().get(0).getId()
                            .equals(constantService.getServiceTypeAnnualSubscription().getId())) {
                        newSubscription.setSubscriptionType(Subscription.ANNUAL_SUBSCRIPTION);

                    } else if (service.getServiceTypes().get(0).getId().equals(constantService
                            .getServiceTypeEnterpriseAnnualSubscription().getId())) {
                        newSubscription.setSubscriptionType(Subscription.ENTERPRISE_ANNUAL_SUBSCRIPTION);

                    } else if (service.getServiceTypes().get(0).getId().equals(constantService
                            .getServiceTypeMonthlySubscription().getId())) {
                        newSubscription.setSubscriptionType(Subscription.MONTHLY_SUBSCRIPTION);

                    } else if (service.getServiceTypes().get(0).getId().equals(constantService
                            .getServiceTypeUniqueArticleBuy().getId())) {
                        newSubscription.setSubscriptionType(Subscription.ONE_POST_SUBSCRIPTION);

                        AssoProvisionPostNewspaper assoProvisionPostNewspaper = assoProvisionPostNewspaperService
                                .getAssoProvisionPostNewspaperByProvision(service.getProvisions().get(0));
                        if (assoProvisionPostNewspaper != null)
                            newSubscription.setPost(assoProvisionPostNewspaper.getPost());

                    } else if (service.getServiceTypes().get(0).getId().equals(constantService
                            .getServiceTypeKioskNewspaperBuy().getId())) {
                        newSubscription.setSubscriptionType(Subscription.NEWSPAPER_KIOSK_BUY);

                        AssoProvisionPostNewspaper assoProvisionPostNewspaper = assoProvisionPostNewspaperService
                                .getAssoProvisionPostNewspaperByProvision(service.getProvisions().get(0));
                        if (assoProvisionPostNewspaper != null)
                            newSubscription.setNewspaper(assoProvisionPostNewspaper.getNewspaper());
                    }

                    if (Boolean.TRUE.equals(service.getAssoAffaireOrder().getCustomerOrder().getIsRecurring())) {
                        newSubscription
                                .setStartDate(
                                        service.getAssoAffaireOrder().getCustomerOrder().getRecurringPeriodStartDate());
                        newSubscription
                                .setEndDate(
                                        service.getAssoAffaireOrder().getCustomerOrder().getRecurringPeriodEndDate());
                    }

                    addOrUpdateSubscription(newSubscription);
                }
            }

            Responsable responsable = responsableService.getResponsable(customerOrder.getId());
            if (responsable != null) {
                responsable.setNumberOfGiftPostsPerMonth(2);
                responsableService.addOrUpdateResponsable(responsable);
            }
        }
    }

    private boolean isJssSubscriptionService(Service service) throws OsirisException {
        Integer subscriptionType = service.getServiceTypes().get(0).getId();

        return subscriptionType.equals(constantService.getServiceTypeAnnualSubscription().getId())
                || subscriptionType.equals(constantService.getServiceTypeEnterpriseAnnualSubscription().getId())
                || subscriptionType.equals(constantService.getServiceTypeMonthlySubscription().getId())
                || subscriptionType.equals(constantService.getServiceTypeKioskNewspaperBuy().getId())
                || subscriptionType.equals(constantService.getServiceTypeUniqueArticleBuy().getId());
    }
}
