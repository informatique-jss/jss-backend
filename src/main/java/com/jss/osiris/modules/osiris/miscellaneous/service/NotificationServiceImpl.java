package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.miscellaneous.repository.NotificationRepository;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;

@org.springframework.stereotype.Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    QuotationService quotationService;

    @Autowired
    ConstantService constantService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    ServiceService serviceService;

    @Autowired
    FormaliteGuichetUniqueStatusService statusService;

    @Override
    public List<Notification> getNotificationsForCurrentEmployee(Boolean displayFuture) {
        Employee currentEmployee = (Employee) employeeService.getCurrentEmployee();
        if (currentEmployee == null)
            return null;
        return notificationRepository.findByEmployees(employeeService.getMyHolidaymaker(currentEmployee),
                currentEmployee, displayFuture);
    }

    @Override
    public Notification getNotification(Integer id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent())
            return notification.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification addOrUpdateNotificationFromUser(
            Notification notification) {
        return addOrUpdateNotification(notification);
    }

    @Override
    public Notification addOrUpdateNotification(
            Notification notification) {
        return notificationRepository.save(notification);
    }

    private void generateNewNotification(Employee fromEmployee, Employee toEmployee, String notificationType,
            boolean showPopup, Service service, Provision provision, CustomerOrder customerOrder) {

        List<Notification> existingNotification = null;
        if (service != null)
            existingNotification = notificationRepository
                    .findByEmployeeAndNotificationTypeAndService(toEmployee, notificationType, service);

        else if (provision != null)
            existingNotification = notificationRepository
                    .findByEmployeeAndNotificationTypeAndProvision(toEmployee, notificationType, provision);

        else if (customerOrder != null)
            existingNotification = notificationRepository
                    .findByEmployeeAndNotificationTypeAndCustomerOrder(toEmployee, notificationType, customerOrder);

        else
            return;

        if (existingNotification == null) {
            existingNotification = new ArrayList<Notification>();
            existingNotification.add(new Notification());
        }

        for (Notification notification : existingNotification) {
            if (notification.getId() == null) {
                notification.setCreatedBy(fromEmployee);
                notification.setCreatedDateTime(LocalDateTime.now());
                notification.setEmployee(toEmployee);
                notification.setCustomerOrder(customerOrder);
                notification.setProvision(provision);
                notification.setService(service);
                notification.setNotificationType(notificationType);
            } else {
                notification.setUpdatedBy(fromEmployee);
                notification.setUpdatedDateTime(LocalDateTime.now());
            }
            notification.setIsRead(false);
            notification.setShowPopup(showPopup);
            addOrUpdateNotification(notification);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification addOrUpdatePersonnalNotification(Notification notification) {
        if (notification != null) {
            if (notification.getId() == null) {
                notification.setEmployee((Employee) employeeService.getCurrentEmployee());
                notification.setCreatedBy((Employee) employeeService.getCurrentEmployee());
                notification.setNotificationType(Notification.PERSONNAL);
                notification.setCreatedDateTime(LocalDateTime.now());
            } else {
                notification.setUpdatedBy(employeeService.getCurrentEmployee());
                notification.setUpdatedDateTime(LocalDateTime.now());
            }
            if (notification.getIsRead() == null)
                notification.setIsRead(false);
            return addOrUpdateNotification(notification);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void purgeNotification() {
        notificationRepository.deleteAll(notificationRepository.findNotificationOlderThanMonths(3));
    }

    @Override
    public void notifyAttachmentAddToProvision(Provision provision, Attachment attachment) throws OsirisException {
        CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
        if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
            if (order != null && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                    || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                if (provision.getAssignedTo() != null) {
                    if (provision.getAssignedTo().getId().equals(employeeService.getCurrentEmployee().getId()))
                        return;

                    generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                            Notification.PROVISION_ADD_ATTACHMENT, false, null, provision, null);
                }
            }
        }
    }

    @Override
    public void notifyAttachmentAddToService(Service service, Attachment attachment) throws OsirisException {
        CustomerOrder order = service.getAssoAffaireOrder().getCustomerOrder();
        List<Integer> employeeIdAlreadyNotified = new ArrayList<Integer>();
        if (order != null && service.getProvisions() != null
                && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                        || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
            for (Provision provision : service.getProvisions()) {
                if (!isProvisionClosed(provision) && !isProvisionOpen(provision)
                        && !employeeIdAlreadyNotified.contains(provision.getAssignedTo().getId())) {
                    if (provision.getAssignedTo() != null) {
                        employeeIdAlreadyNotified.add(provision.getAssignedTo().getId());
                        if (provision.getAssignedTo().getId().equals(employeeService.getCurrentEmployee().getId()))
                            return;

                        generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                                Notification.SERVICE_ADD_ATTACHMENT, false, service, null, null);
                    }
                }
            }
        }
    }

    @Override
    public void notifyAttachmentAddToCustomerorder(CustomerOrder order, Attachment attachment) throws OsirisException {
        List<Integer> employeeIdAlreadyNotified = new ArrayList<Integer>();
        if (order != null && order.getAssoAffaireOrders() != null) {
            for (AssoAffaireOrder asso : order.getAssoAffaireOrders())
                if (asso.getServices() != null)
                    for (Service service : asso.getServices())
                        if (service.getProvisions() != null
                                && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                                        || order.getCustomerOrderStatus().getCode()
                                                .equals(CustomerOrderStatus.TO_BILLED))) {
                            for (Provision provision : service.getProvisions()) {
                                if (!isProvisionClosed(provision) && !isProvisionOpen(provision)
                                        && !employeeIdAlreadyNotified.contains(provision.getAssignedTo().getId())) {
                                    if (provision.getAssignedTo() != null) {
                                        employeeIdAlreadyNotified.add(provision.getAssignedTo().getId());
                                        if (provision.getAssignedTo().getId()
                                                .equals(employeeService.getCurrentEmployee().getId()))
                                            return;

                                        generateNewNotification(employeeService.getCurrentEmployee(),
                                                provision.getAssignedTo(),
                                                Notification.ORDER_ADD_ATTACHMENT, false, null, null, order);
                                    }
                                }
                            }
                        }
        }
    }

    private boolean isProvisionClosed(Provision provision) {
        if (provision.getAnnouncement() != null)
            return provision.getAnnouncement().getAnnouncementStatus().getIsCloseState();
        if (provision.getSimpleProvision() != null)
            return provision.getSimpleProvision().getSimpleProvisionStatus().getIsCloseState();
        if (provision.getFormalite() != null)
            return provision.getFormalite().getFormaliteStatus().getIsCloseState();
        if (provision.getDomiciliation() != null)
            return provision.getDomiciliation().getDomiciliationStatus().getIsCloseState();
        return false;
    }

    private boolean isProvisionOpen(Provision provision) {
        if (provision.getAnnouncement() != null)
            return provision.getAnnouncement().getAnnouncementStatus().getIsOpenState();
        if (provision.getSimpleProvision() != null)
            return provision.getSimpleProvision().getSimpleProvisionStatus().getIsOpenState();
        if (provision.getFormalite() != null)
            return provision.getFormalite().getFormaliteStatus().getIsOpenState();
        if (provision.getDomiciliation() != null)
            return provision.getDomiciliation().getDomiciliationStatus().getIsOpenState();
        return false;
    }

    @Override
    public void notifyGuichetUniqueFormaliteStatus(Provision provision)
            throws OsirisException {
        CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
        if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
            if (order != null && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                    || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                if (provision.getAssignedTo() != null) {
                    if (provision.getAssignedTo().getId().equals(employeeService.getCurrentEmployee().getId()))
                        return;

                    generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                            Notification.PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED, false, null, provision, null);
                }
            }
        }
    }

    @Override
    public void notifyGuichetUniqueFormaliteSigned(Provision provision)
            throws OsirisException {
        CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
        if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
            if (order != null && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                    || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                if (provision.getAssignedTo() != null) {
                    if (provision.getAssignedTo().getId().equals(employeeService.getCurrentEmployee().getId()))
                        return;

                    generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                            Notification.PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED, false, null, provision, null);
                }
            }
        }
    }
}
