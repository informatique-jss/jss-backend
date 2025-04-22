package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;

public interface NotificationService {
        public List<Notification> getNotificationsForCurrentEmployee(Boolean displayFuture);

        public Notification getNotification(Integer id);

        public Notification addOrUpdateNotificationFromUser(Notification notification);

        public Notification addOrUpdateNotification(Notification notification);

        public void deleteNotification(Notification notification);

        public void notifyAttachmentAddToProvision(Provision provision, Attachment attachment) throws OsirisException;

        public void notifyAttachmentAddToService(Service service, Attachment attachment)
                        throws OsirisException;

        public void notifyAttachmentAddToCustomerorder(CustomerOrder order, Attachment attachment)
                        throws OsirisException;

        public void notifyGuichetUniqueFormaliteStatus(Provision provision) throws OsirisException;

        public void notifyGuichetUniqueFormaliteSigned(Provision provision) throws OsirisException;

        public Notification addOrUpdatePersonnalNotification(Notification notification);

        public void purgeNotification();
}
