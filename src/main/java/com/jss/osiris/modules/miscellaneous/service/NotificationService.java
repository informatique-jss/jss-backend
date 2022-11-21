package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;

public interface NotificationService {
        public List<Notification> getNotificationsForCurrentEmployee(Boolean displayFuture);

        public Notification getNotification(Integer id);

        public Notification addOrUpdateNotificationFromUser(Notification notification);

        public Notification addOrUpdateNotification(Notification notification);

        public void deleteNotification(Notification notification);

        public Notification notifyNewQuotation(Quotation quotation) throws OsirisException;

        public Notification notifyQuotationToVerify(Quotation quotation) throws OsirisException;

        public Notification notifyQuotationSent(Quotation quotation) throws OsirisException;

        public Notification notifyQuotationValidatedByCustomer(Quotation quotation) throws OsirisException;

        public Notification notifyQuotationRefusedByCustomer(Quotation quotation) throws OsirisException;

        public List<Notification> notifyNewCustomerOrderQuotation(CustomerOrder customerOrder) throws OsirisException;

        public List<Notification> notifyCustomerOrderToVerify(CustomerOrder customerOrder) throws OsirisException;

        public List<Notification> notifyCustomerOrderToBeingProcessed(CustomerOrder customerOrder, boolean isFromHuman)
                        throws OsirisException;

        public List<Notification> notifyCustomerOrderToBeingToBilled(CustomerOrder customerOrder)
                        throws OsirisException;

        public Notification addOrUpdatePersonnalNotification(Notification notifications);

        public void purgeNotification();
}
