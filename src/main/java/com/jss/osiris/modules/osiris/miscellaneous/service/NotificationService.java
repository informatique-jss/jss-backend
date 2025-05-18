package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReport;

public interface NotificationService {
        public List<Notification> getNotificationsForCurrentEmployee(Boolean displayFuture, Boolean displayRead,
                        List<String> notificationTypes, Boolean onlyForNumber, Boolean completeAdditionnalInformation);

        public Notification getNotification(Integer id);

        public Notification addOrUpdateNotificationFromUser(Notification notification);

        public Notification addOrUpdateNotification(Notification notification);

        public void deleteNotification(Notification notification);

        public void notifyAttachmentAddToProvision(Provision provision, Attachment attachment) throws OsirisException;

        public void notifyAttachmentAddToService(Service service, Attachment attachment)
                        throws OsirisException;

        public void notifyAttachmentAddToCustomerorder(CustomerOrder order, Attachment attachment)
                        throws OsirisException;

        public void notifyGuichetUniqueFormaliteStatusValidated(Provision provision) throws OsirisException;

        public void notifyGuichetUniqueFormaliteStatusRefused(Provision provision) throws OsirisException;

        public void notifyGuichetUniqueFormaliteSigned(Provision provision) throws OsirisException;

        public Notification addOrUpdatePersonnalNotification(Notification notification);

        public void purgeNotification();

        public List<String> getAllNotificationTypes();

        public List<Notification> getNotificationsForCustomerOrder(Integer customerOrderId);

        public List<Notification> getNotificationsForQuotation(Integer quotationId);

        public List<Notification> getNotificationsForProvision(Integer provisionId);

        public List<Notification> getNotificationsForInvoice(Integer invoiceId);

        public List<Notification> getNotificationsForService(Integer serviceId);

        public List<Notification> getNotificationsForAffaire(Integer affaireId);

        public List<Notification> getNotificationsForTiers(Integer tiersId);

        public List<Notification> getNotificationsForResponsable(Integer responsableId);

        public void notifyNewCandidacy(Candidacy candidacy) throws OsirisException;

        public void notifyIncidentReportAsked(IncidentReport incident) throws OsirisException;
}
