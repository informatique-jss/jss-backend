package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface SubscriptionService {

        public List<Subscription> getSubscriptionsForMail(Mail mail);

        public Subscription getSubscriptionByToken(String token);

        public Subscription addOrUpdateSubscription(Subscription subscription);

        public Subscription givePostSubscription(Post postToOffer, Mail recipientMail) throws OsirisException;

        public Integer getRemainingPostToShareForCurrentMonth(Responsable signedInUser);

        public boolean canResponsableSeeKioskNewspaper(Responsable responsable, Newspaper newspaper);

        public boolean isResponsableHasFullValidSubscription(Responsable responsable, List<Subscription> subscriptions);
}
