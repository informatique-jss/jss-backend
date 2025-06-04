package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface SubscriptionService {

        public List<Subscription> getSubscriptionsForMail(Mail mail);

        public Subscription getSubscriptionByToken(String token);

        public Subscription addOrUpdateSubscription(Subscription subscription);

        public Subscription givePostSubscription(Post postToOffer, Mail recipientMail);

        public Integer getNumberOfPostSharedOnMonth(Mail responsableMail);
}
