package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.miscellaneous.repository.NotificationRepository;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.BaloNotice;
import com.jss.osiris.modules.osiris.quotation.model.BodaccNotice;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.JoNotice;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReport;

@org.springframework.stereotype.Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    ConstantService constantService;

    @Autowired
    ProvisionService provisionService;

    @Override
    public List<Notification> getNotificationsForCurrentEmployee(Boolean displayFuture, Boolean displayRead,
            List<String> notificationTypes, Boolean onlyForNumber, Boolean completeAdditionnalInformation)
            throws OsirisException {
        Employee currentEmployee = (Employee) employeeService.getCurrentEmployee();
        if (currentEmployee == null)
            return null;
        if (notificationTypes == null || notificationTypes.size() == 0)
            notificationTypes = getAllNotificationTypes();

        List<Notification> notifications = notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(currentEmployee), displayFuture, displayRead,
                getNotificationTypesToHideForCurrentUser(), notificationTypes);

        if (onlyForNumber)
            return notifications.stream().filter(n -> n.getIsRead() != null && n.getIsRead() == false).toList();

        return completeNotifications(notifications, completeAdditionnalInformation);
    }

    private List<String> getNotificationTypesToHideForCurrentUser() {
        Employee currentEmployee = (Employee) employeeService.getCurrentEmployee();
        List<String> notificationTypesToHide = currentEmployee.getNotificationTypeToHide();
        if (notificationTypesToHide == null || notificationTypesToHide.size() == 0)
            notificationTypesToHide = new ArrayList<String>();
        return notificationTypesToHide;
    }

    private List<Notification> completeNotifications(List<Notification> notifications,
            Boolean completeAdditionnalInformation) throws OsirisException {
        if (notifications != null) {
            List<Notification> ouNotifications = new ArrayList<Notification>();
            for (Notification notificationIn : notifications) {
                Notification notification = cloneNotification(notificationIn);
                if (notification.getProvision() != null) {
                    notification.setService(notification.getProvision().getService());
                }
                if (notification.getService() != null) {
                    notification.setCustomerOrder(notification.getService().getAssoAffaireOrder().getCustomerOrder());
                }
                if (notification.getInvoice() != null) {
                    if (notification.getInvoice().getCustomerOrder() != null) {
                        notification.setCustomerOrder(notification.getInvoice().getCustomerOrder());
                    } else if (notification.getInvoice().getCustomerOrderForInboundInvoice() != null) {
                        notification.setCustomerOrder(notification.getInvoice().getCustomerOrderForInboundInvoice());
                    }
                }
                if (notification.getCustomerOrder() != null && completeAdditionnalInformation) {
                    notification.getCustomerOrder().setServicesList(customerOrderService
                            .completeAdditionnalInformationForCustomerOrder(notification.getCustomerOrder(), false)
                            .getServicesList());
                    notification.getCustomerOrder().setAffairesList(customerOrderService
                            .completeAdditionnalInformationForCustomerOrder(notification.getCustomerOrder(), false)
                            .getAffairesList());
                }
                ouNotifications.add(notification);
            }
            return ouNotifications;
        }
        return notifications;
    }

    @Override
    public Notification getNotification(Integer id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent())
            return notification.get();
        return null;
    }

    @Override
    public List<String> getAllNotificationTypes() {
        return Notification.getNotificationTypes();
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
            boolean showPopup, Service service, Provision provision, CustomerOrder customerOrder, Candidacy candidacy) {

        if (toEmployee == null)
            return;

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

        else if (candidacy != null)
            existingNotification = notificationRepository.findByEmployeeAndNotificationTypeAndCandidacy(toEmployee,
                    notificationType, candidacy);
        else
            return;

        if (existingNotification == null || existingNotification.size() == 0) {
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
                notification.setCandidacy(candidacy);
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
    public List<Notification> getNotificationsForCustomerOrder(Integer customerOrderId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getCustomerOrder() != null && n.getCustomerOrder().getId().equals(customerOrderId))
                .toList(), true);
    }

    @Override
    public List<Notification> getNotificationsForQuotation(Integer quotationId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getQuotation() != null && n.getQuotation().getId().equals(quotationId))
                .toList(), true);
    }

    @Override
    public List<Notification> getNotificationsForService(Integer serviceId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getService() != null && n.getService().getId().equals(serviceId))
                .toList(), true);
    }

    @Override
    public List<Notification> getNotificationsForProvision(Integer provisionId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getProvision() != null && n.getProvision().getId().equals(provisionId))
                .toList(), true);
    }

    @Override
    public List<Notification> getNotificationsForInvoice(Integer invoiceId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getInvoice() != null && n.getInvoice().getId().equals(invoiceId))
                .toList(), true);
    }

    @Override
    public List<Notification> getNotificationsForAffaire(Integer affaireId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getAffaire() != null && n.getAffaire().getId().equals(affaireId))
                .toList(), true);
    }

    @Override
    public List<Notification> getNotificationsForTiers(Integer tiersId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getTiers() != null && n.getTiers().getId().equals(tiersId))
                .toList(), true);
    }

    @Override
    public List<Notification> getNotificationsForResponsable(Integer responsableId) throws OsirisException {
        return completeNotifications(notificationRepository.findByEmployees(
                employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee()), true, false,
                getNotificationTypesToHideForCurrentUser(), getAllNotificationTypes()).stream()
                .filter(n -> n.getResponsable() != null && n.getResponsable().getId().equals(responsableId))
                .toList(), true);
    }

    @Override
    public void notifyAttachmentAddToProvision(Provision provision, Attachment attachment) throws OsirisException {
        provision = provisionService.getProvision(provision.getId());
        if (provision.getService() != null) {
            CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
            if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
                if (order != null
                        && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                                || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                    if (provision.getAssignedTo() != null) {
                        if (provision.getAssignedTo().getId().equals(employeeService.getCurrentEmployee().getId()))
                            return;

                        generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                                Notification.PROVISION_ADD_ATTACHMENT, false, null, provision, null, null);
                    }
                }
            }
        }
    }

    @Override
    public void notifyBodaccNoticeAddToProvision(Provision provision, BodaccNotice notice) {
        provision = provisionService.getProvision(provision.getId());
        if (provision.getService() != null) {
            CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
            if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
                if (order != null
                        && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                                || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                    if (provision.getAssignedTo() != null) {

                        generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                                Notification.PROVISION_ADD_BODACC_NOTICE, false, null, provision, null, null);
                    }
                }
            }
        }
    }

    @Override
    public void notifyBaloNoticeAddToProvision(Provision provision, BaloNotice notice) {
        provision = provisionService.getProvision(provision.getId());
        if (provision.getService() != null) {
            CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
            if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
                if (order != null
                        && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                                || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                    if (provision.getAssignedTo() != null) {

                        generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                                Notification.PROVISION_ADD_BALO_NOTICE, false, null, provision, null, null);
                    }
                }
            }
        }
    }

    @Override
    public void notifyJoNoticeAddToProvision(Provision provision, JoNotice notice) {
        provision = provisionService.getProvision(provision.getId());
        if (provision.getService() != null) {
            CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
            if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
                if (order != null
                        && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                                || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                    if (provision.getAssignedTo() != null) {

                        generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                                Notification.PROVISION_ADD_JO_NOTICE, false, null, provision, null, null);
                    }
                }
            }
        }
    }

    @Override
    public void notifyIncidentReportAsked(IncidentReport incident) throws OsirisException {
        generateNewNotification(incident.getInitiatedBy(), incident.getAssignedTo(), Notification.INCIDENT_REPORT_ASKED,
                false, null, incident.getProvision(), null, null);
    }

    @Override
    public void notifyQuotationModified(CustomerOrder customerOrder) {
        generateNewNotification(employeeService.getCurrentEmployee(), customerOrder.getResponsable().getSalesEmployee(),
                Notification.MODIFIED_QUOTATION, false, null, null, customerOrder, null);
    }

    @Override
    public void notifyAttachmentAddToService(Service service, Attachment attachment) throws OsirisException {
        CustomerOrder order = service.getAssoAffaireOrder().getCustomerOrder();
        List<Integer> employeeIdAlreadyNotified = new ArrayList<Integer>();
        if (order != null && service.getProvisions() != null
                && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                        || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
            for (Provision provision : service.getProvisions()) {
                if (!isProvisionClosed(provision) && !isProvisionOpen(provision) && provision.getAssignedTo() != null
                        && !employeeIdAlreadyNotified.contains(provision.getAssignedTo().getId())) {
                    if (provision.getAssignedTo() != null) {
                        employeeIdAlreadyNotified.add(provision.getAssignedTo().getId());
                        if (employeeService.getCurrentEmployee() != null && provision.getAssignedTo().getId()
                                .equals(employeeService.getCurrentEmployee().getId()))
                            return;

                        generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                                Notification.SERVICE_ADD_ATTACHMENT, false, service, null, null, null);
                    }
                }
            }
        }
    }

    @Override
    public void notifyInformationAddToService(Service service) throws OsirisException {
        CustomerOrder order = service.getAssoAffaireOrder().getCustomerOrder();
        List<Integer> employeeIdAlreadyNotified = new ArrayList<Integer>();
        if (order != null && service.getProvisions() != null
                && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                        || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
            for (Provision provision : service.getProvisions()) {
                if (!isProvisionClosed(provision) && provision.getAssignedTo() != null
                        && !employeeIdAlreadyNotified.contains(provision.getAssignedTo().getId())) {
                    if (provision.getAssignedTo() != null) {
                        employeeIdAlreadyNotified.add(provision.getAssignedTo().getId());
                        if (employeeService.getCurrentEmployee() != null && provision.getAssignedTo().getId()
                                .equals(employeeService.getCurrentEmployee().getId()))
                            return;

                        generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                                Notification.SERVICE_ADD_INFORMATION, false, service, null, null, null);
                    }
                }
            }
        }
    }

    @Override
    public void notifyAttachmentAddToCustomerOrder(CustomerOrder order, Attachment attachment) throws OsirisException {
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
                                        && provision.getAssignedTo() != null
                                        && !employeeIdAlreadyNotified.contains(provision.getAssignedTo().getId())) {
                                    if (provision.getAssignedTo() != null) {
                                        employeeIdAlreadyNotified.add(provision.getAssignedTo().getId());
                                        if (employeeService.getCurrentEmployee() != null
                                                && provision.getAssignedTo().getId()
                                                        .equals(employeeService.getCurrentEmployee().getId()))
                                            return;

                                        generateNewNotification(employeeService.getCurrentEmployee(),
                                                provision.getAssignedTo(),
                                                Notification.ORDER_ADD_ATTACHMENT, false, null, null, order, null);
                                    }
                                }
                            }
                        }
        }
    }

    @Override
    public void notifyCommentFromMyJssAddToCustomerOrder(CustomerOrder order)
            throws OsirisException {
        if (order.getResponsable() != null && order.getResponsable().getSalesEmployee() != null
                && employeeService.getCurrentMyJssUser() != null)
            generateNewNotification(null, order.getResponsable().getSalesEmployee(),
                    Notification.ORDER_ADD_COMMENT_MYJSS, false, null, null, order, null);
    }

    @Override
    public void notifyImmediateAffactationOfOrder(CustomerOrder customerOrder) throws OsirisException {
        generateNewNotification(employeeService.getCurrentEmployee(), constantService.getEmployeeProductionDirector(),
                Notification.ORDER_IMMEDIATLY_AFFECTED, false, null, null, customerOrder, null);
    }

    private boolean isProvisionClosed(Provision provision) {
        if (provision.getAnnouncement() != null && provision.getAnnouncement().getAnnouncementStatus() != null)
            return provision.getAnnouncement().getAnnouncementStatus().getIsCloseState();
        if (provision.getSimpleProvision() != null && provision.getSimpleProvision().getSimpleProvisionStatus() != null)
            return provision.getSimpleProvision().getSimpleProvisionStatus().getIsCloseState();
        if (provision.getFormalite() != null && provision.getFormalite().getFormaliteStatus() != null)
            return provision.getFormalite().getFormaliteStatus().getIsCloseState();
        if (provision.getDomiciliation() != null && provision.getDomiciliation().getDomiciliationStatus() != null)
            return provision.getDomiciliation().getDomiciliationStatus().getIsCloseState();
        return false;
    }

    private boolean isProvisionOpen(Provision provision) {
        if (provision.getAnnouncement() != null && provision.getAnnouncement().getAnnouncementStatus() != null)
            return provision.getAnnouncement().getAnnouncementStatus().getIsOpenState();
        if (provision.getSimpleProvision() != null && provision.getSimpleProvision().getSimpleProvisionStatus() != null)
            return provision.getSimpleProvision().getSimpleProvisionStatus().getIsOpenState();
        if (provision.getFormalite() != null && provision.getFormalite().getFormaliteStatus() != null)
            return provision.getFormalite().getFormaliteStatus().getIsOpenState();
        if (provision.getDomiciliation() != null && provision.getDomiciliation().getDomiciliationStatus() != null)
            return provision.getDomiciliation().getDomiciliationStatus().getIsOpenState();
        return false;
    }

    @Override
    public void notifyGuichetUniqueFormaliteStatusValidated(Provision provision)
            throws OsirisException {
        CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
        if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
            if (order != null && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                    || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                        Notification.PROVISION_GUICHET_UNIQUE_STATUS_VALIDATED, false, null, provision, null, null);
            }
        }
    }

    @Override
    public void notifyGuichetUniqueFormaliteStatusRefused(Provision provision)
            throws OsirisException {
        CustomerOrder order = provision.getService().getAssoAffaireOrder().getCustomerOrder();
        if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
            if (order != null && (order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)
                    || order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.TO_BILLED))) {
                generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                        Notification.PROVISION_GUICHET_UNIQUE_STATUS_REFUSED, false, null, provision, null, null);
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
                generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                        Notification.PROVISION_GUICHET_UNIQUE_STATUS_SIGNED, false, null, provision, null, null);
            }
        }
    }

    @Override
    public void notifyNewCandidacy(Candidacy candidacy) throws OsirisException {
        generateNewNotification(null, constantService.getEmployeeCandidacyResponsible(),
                Notification.NEW_CANDIDACY_RECEIVED, false, null, null, null, candidacy);
    }

    public Notification cloneNotification(Notification original) {
        if (original == null) {
            return null;
        }

        Notification clone = new Notification();

        clone.setId(original.getId());
        clone.setIsRead(original.getIsRead());
        clone.setCreatedDateTime(original.getCreatedDateTime());
        clone.setUpdatedDateTime(original.getUpdatedDateTime());
        clone.setNotificationType(original.getNotificationType());
        clone.setDetail1(original.getDetail1());
        clone.setSummary(original.getSummary());
        clone.setShowPopup(original.getShowPopup());
        clone.setEmployee(original.getEmployee());
        clone.setCreatedBy(original.getCreatedBy());
        clone.setUpdatedBy(original.getUpdatedBy());
        clone.setService(original.getService());
        clone.setProvision(original.getProvision());
        clone.setCustomerOrder(original.getCustomerOrder());
        clone.setInvoice(original.getInvoice());
        clone.setQuotation(original.getQuotation());
        clone.setTiers(original.getTiers());
        clone.setResponsable(original.getResponsable());
        clone.setAffaire(original.getAffaire());

        return clone;
    }
}
