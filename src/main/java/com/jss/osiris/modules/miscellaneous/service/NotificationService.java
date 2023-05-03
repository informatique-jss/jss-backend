package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

public interface NotificationService {
        public List<Notification> getNotificationsForCurrentEmployee(Boolean displayFuture);

        public Notification getNotification(Integer id);

        public Notification addOrUpdateNotificationFromUser(Notification notification);

        public Notification addOrUpdateNotification(Notification notification);

        public void deleteNotification(Notification notification);

        public void notifyNewQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException;

        public void notifyQuotationToVerify(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException;

        public void notifyQuotationSent(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException;

        public void notifyQuotationValidatedByCustomer(Quotation quotation, boolean isFromHuman)
                        throws OsirisException, OsirisClientMessageException;

        public void notifyQuotationRefusedByCustomer(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException;

        public void notifyNewCustomerOrderQuotation(CustomerOrder customerOrder) throws OsirisException;

        public void notifyCustomerOrderToVerify(CustomerOrder customerOrder) throws OsirisException;

        public void notifyCustomerOrderToBeingProcessed(CustomerOrder customerOrder, boolean isFromHuman)
                        throws OsirisException;

        public void notifyCustomerOrderToBeingProcessedFromDeposit(CustomerOrder customerOrder, boolean isFromHuman)
                        throws OsirisException;

        public void notifyCustomerOrderToBeingToBilled(CustomerOrder customerOrder)
                        throws OsirisException;

        public Notification addOrUpdatePersonnalNotification(Notification notifications);

        public void purgeNotification();

        public void notifyInvoiceToReminder(Invoice invoice) throws OsirisException;

        public void notifyAttachmentAddToProvision(Provision provision, Attachment attachment) throws OsirisException;

        public void notifyGuichetUniqueFormaliteStatus(Provision provision,
                        FormaliteGuichetUnique formaliteGuichetUnique) throws OsirisException;
}
