package com.jss.osiris.modules.miscellaneous.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.miscellaneous.repository.NotificationRepository;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Status;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatusService;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
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
    StatusService statusService;

    @Override
    public List<Notification> getNotificationsForCurrentEmployee(Boolean displayFuture) {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        if (currentEmployee == null)
            return null;
        return notificationRepository
                .findByEmployees(employeeService.getMyHolidaymaker(currentEmployee), currentEmployee,
                        displayFuture);
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

    private Notification generateNewNotification(Employee fromEmployee, Employee toEmployee, String notificationType,
            IId entity, String detail1, String title, boolean showPopup) {
        Notification notification = new Notification();
        notification.setCreatedBy(fromEmployee);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setEmployee(toEmployee);
        notification.setEntityId(entity.getId());

        Object entityObject = null;

        if (entity != null && entity.getClass().getSimpleName().contains("HibernateProxy"))
            entityObject = Hibernate.unproxy(entity);
        else
            entityObject = entity;

        if (entityObject != null)
            notification.setEntityType(entityObject.getClass().getSimpleName());
        notification.setIsRead(false);
        notification.setNotificationType(notificationType);
        notification.setDetail1(detail1);
        notification.setShowPopup(showPopup);
        notification.setSummary(title);
        return addOrUpdateNotification(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }

    private Notification genericNotificationForQuotation(Quotation quotation, String notificationType,
            boolean isFromHuman)
            throws OsirisException, OsirisClientMessageException {
        ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);

        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee());

        if (compareEmployee != null)
            for (Employee employee : compareEmployee)
                if (employee.getId().equals(quotation.getAssignedTo().getId()))
                    createdByMe = true;

        String customerOrderName = "";
        if (customerOrder instanceof Responsable)
            customerOrderName = ((Responsable) customerOrder).getCivility().getLabel() + " "
                    + ((Responsable) customerOrder).getFirstname() + " " + ((Responsable) customerOrder).getLastname();
        if (customerOrder instanceof Tiers)
            if (((Tiers) customerOrder).getDenomination() != null)
                customerOrderName = ((Tiers) customerOrder).getDenomination();
            else
                customerOrderName = ((Tiers) customerOrder).getCivility().getLabel() + " "
                        + ((Tiers) customerOrder).getFirstname() + " " + ((Tiers) customerOrder).getLastname();
        if (customerOrder instanceof Confrere)
            customerOrderName = ((Confrere) customerOrder).getLabel();

        if (!createdByMe)
            return generateNewNotification(!isFromHuman ? null : employeeService.getCurrentEmployee(),
                    quotation.getAssignedTo(),
                    notificationType,
                    quotation, customerOrderName, null, false);

        return null;
    }

    @Override
    public void notifyNewQuotation(Quotation quotation) throws OsirisException, OsirisClientMessageException {
        // return genericNotificationForQuotation(quotation,
        // Notification.QUOTATION_CREATE);
    }

    @Override
    public void notifyQuotationToVerify(Quotation quotation)
            throws OsirisException, OsirisClientMessageException {
        // return genericNotificationForQuotation(quotation,
        // Notification.QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY);
    }

    @Override
    public void notifyQuotationSent(Quotation quotation) throws OsirisException, OsirisClientMessageException {
        // return genericNotificationForQuotation(quotation,
        // Notification.QUOTATION_SENT);
    }

    @Override
    public void notifyQuotationValidatedByCustomer(Quotation quotation, boolean isFromHuman)
            throws OsirisException, OsirisClientMessageException {
        genericNotificationForQuotation(quotation, Notification.QUOTATION_VALIDATED_BY_CUSOMER, isFromHuman);
    }

    @Override
    public void notifyQuotationRefusedByCustomer(Quotation quotation)
            throws OsirisException, OsirisClientMessageException {
        // return genericNotificationForQuotation(quotation,
        // Notification.QUOTATION_REFUSED_BY_CUSOMER);
    }

    private List<Notification> genericNotificationForCustomerOrder(CustomerOrder customerOrder, String notificationType,
            boolean notifyAffaireResponsibles, boolean notifySalesEmployee, boolean notifiyBillingResponsible,
            boolean isFromHuman) throws OsirisException {
        ITiers customerOrderTiers = quotationService.getCustomerOrderOfQuotation(customerOrder);

        ArrayList<Notification> notifications = new ArrayList<Notification>();

        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee());

        String customerOrderName = "";
        if (customerOrderTiers instanceof Responsable)
            customerOrderName = ((Responsable) customerOrderTiers).getCivility().getLabel() + " "
                    + ((Responsable) customerOrderTiers).getFirstname() + " "
                    + ((Responsable) customerOrderTiers).getLastname();
        if (customerOrderTiers instanceof Tiers)
            if (((Tiers) customerOrderTiers).getDenomination() != null)
                customerOrderName = ((Tiers) customerOrderTiers).getDenomination();
            else
                customerOrderName = ((Tiers) customerOrderTiers).getCivility().getLabel() + " "
                        + ((Tiers) customerOrderTiers).getFirstname() + " "
                        + ((Tiers) customerOrderTiers).getLastname();
        if (customerOrderTiers instanceof Confrere)
            customerOrderName = ((Confrere) customerOrderTiers).getLabel();

        if (notifySalesEmployee) {
            if (compareEmployee != null)
                for (Employee employee : compareEmployee)
                    if (employee.getId().equals(customerOrder.getAssignedTo().getId()))
                        createdByMe = true;

            if (!createdByMe)
                notifications.add(
                        generateNewNotification(
                                isFromHuman && employeeService.getCurrentEmployee() != null
                                        ? employeeService.getCurrentEmployee()
                                        : null,
                                customerOrder.getAssignedTo(),
                                notificationType, customerOrder, customerOrderName, null, false));
        }

        if (notifiyBillingResponsible) {
            if (compareEmployee != null)
                for (Employee employee : compareEmployee)
                    if (employee.getId().equals(constantService.getEmployeeBillingResponsible().getId()))
                        createdByMe = true;

            if (!createdByMe)
                notifications.add(
                        generateNewNotification(
                                isFromHuman && employeeService.getCurrentEmployee() != null
                                        ? employeeService.getCurrentEmployee()
                                        : null,
                                constantService.getEmployeeBillingResponsible(),
                                notificationType, customerOrder, customerOrderName, null, false));
        }

        if (notifyAffaireResponsibles)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                createdByMe = false;
                if (asso.getAssignedTo() != null) {
                    if (compareEmployee != null)
                        for (Employee employee : compareEmployee)
                            if (employee.getId().equals(asso.getAssignedTo().getId()))
                                createdByMe = true;
                    if (!createdByMe) {
                        notifications.add(
                                generateNewNotification(
                                        isFromHuman && employeeService.getCurrentEmployee() != null
                                                ? employeeService.getCurrentEmployee()
                                                : null,
                                        asso.getAssignedTo(),
                                        notificationType, customerOrder, customerOrderName, null, false));
                        // Do not notify twice
                        if (compareEmployee != null)
                            compareEmployee.add(asso.getAssignedTo());
                    }
                }
            }
        return notifications;
    }

    @Override
    public void notifyNewCustomerOrderQuotation(CustomerOrder customerOrder) throws OsirisException {
        // return genericNotificationForCustomerOrder(customerOrder,
        // Notification.CUSTOMER_ORDER_CREATE, false, true,false,true);
    }

    @Override
    public void notifyCustomerOrderToVerify(CustomerOrder customerOrder) throws OsirisException {
        // return genericNotificationForCustomerOrder(customerOrder,
        // Notification.CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY,true, true, false,
        // true);
    }

    @Override
    public void notifyCustomerOrderToBeingProcessed(CustomerOrder customerOrder, boolean isFromHuman)
            throws OsirisException {
        // ArrayList<Notification> notifications = new ArrayList<Notification>();
        // notifications.addAll(
        // genericNotificationForCustomerOrder(customerOrder,
        // Notification.CUSTOMER_ORDER_BEING_PROCESSED, false,
        // true, false, isFromHuman));
        // notifications.addAll(genericNotificationForCustomerOrder(customerOrder,
        // Notification.CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN, true, false, false,
        // isFromHuman));
        // return notifications;
    }

    @Override
    public void notifyCustomerOrderToBeingProcessedFromDeposit(CustomerOrder customerOrder, boolean isFromHuman)
            throws OsirisException {
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        notifications.addAll(
                genericNotificationForCustomerOrder(customerOrder,
                        Notification.CUSTOMER_ORDER_BEING_PROCESSED_FROM_DEPOSIT, true,
                        false, false, isFromHuman));
        // return notifications;
    }

    @Override
    public void notifyCustomerOrderToBeingToBilled(CustomerOrder customerOrder) throws OsirisException {
        // return genericNotificationForCustomerOrder(customerOrder,
        // Notification.CUSTOMER_ORDER_TO_BE_BILLED,false, false, true, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification addOrUpdatePersonnalNotification(Notification notification) {
        if (notification != null) {
            notification.setEmployee(employeeService.getCurrentEmployee());
            notification.setEntityId(null);
            notification.setEntityType(null);
            if (notification.getIsRead() == null)
                notification.setIsRead(false);
            notification.setCreatedBy(employeeService.getCurrentEmployee());
            notification.setNotificationType(Notification.PERSONNAL);
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
    public void notifyInvoiceToReminder(Invoice invoice) throws OsirisException {
        // return genericNotificationForInvoice(invoice,
        // Notification.INVOICE_REMINDER_PAYMENT, true);
    }

    /*
     * private List<Notification> genericNotificationForInvoice(Invoice invoice,
     * String notificationType,
     * boolean notifiyInvoiceReminderResponsible) throws OsirisException {
     * ArrayList<Notification> notifications = new ArrayList<Notification>();
     * 
     * notifications.add(generateNewNotification(null,
     * constantService.getEmployeeInvoiceReminderResponsible(),
     * notificationType, invoice, null, null, false));
     * 
     * return notifications;
     * }
     */

    @Override
    public void notifyAttachmentAddToProvision(Provision provision, Attachment attachment) throws OsirisException {
        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee());
        provision = provisionService.getProvision(provision.getId());

        if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
            if (compareEmployee != null)
                for (Employee employee : compareEmployee)
                    if (provision.getAssignedTo() != null && employee.getId().equals(provision.getAssignedTo().getId()))
                        createdByMe = true;

            if (!createdByMe && provision.getAssignedTo() != null) {
                String details = "";
                Affaire affaire = provision.getAssoAffaireOrder().getAffaire();
                if (affaire != null)
                    details += affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname());
                details += " - ";
                if (provision.getProvisionFamilyType() != null && provision.getProvisionType() != null)
                    details += provision.getProvisionFamilyType().getLabel() + " - "
                            + provision.getProvisionType().getLabel();
                details += " - ";
                details += attachment.getAttachmentType().getLabel() + " (" + attachment.getDescription() + ")";
                generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                        Notification.PROVISION_ADD_ATTACHMENT, provision, details, null, false);
            }
        }
    }

    private boolean isProvisionClosed(Provision provision) {
        if (provision.getAnnouncement() != null)
            return provision.getAnnouncement().getAnnouncementStatus().getIsCloseState();
        if (provision.getBodacc() != null)
            return provision.getBodacc().getBodaccStatus().getIsCloseState();
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
        if (provision.getBodacc() != null)
            return provision.getBodacc().getBodaccStatus().getIsOpenState();
        if (provision.getSimpleProvision() != null)
            return provision.getSimpleProvision().getSimpleProvisionStatus().getIsOpenState();
        if (provision.getFormalite() != null)
            return provision.getFormalite().getFormaliteStatus().getIsOpenState();
        if (provision.getDomiciliation() != null)
            return provision.getDomiciliation().getDomiciliationStatus().getIsOpenState();
        return false;
    }

    @Override
    public void notifyGuichetUniqueFormaliteStatus(Provision provision, FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException {
        provision = provisionService.getProvision(provision.getId());

        if (!isProvisionClosed(provision) && provision.getAssoAffaireOrder() != null
                && provision.getAssoAffaireOrder().getCustomerOrder() != null
                && !provision.getAssoAffaireOrder().getCustomerOrder().getCustomerOrderStatus().getCode()
                        .equals(CustomerOrderStatus.OPEN)) {

            if (provision.getAssignedTo() != null) {
                String details = "";
                Affaire affaire = provision.getAssoAffaireOrder().getAffaire();
                if (affaire != null)
                    details += affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname());
                details += " - ";
                if (provision.getProvisionFamilyType() != null && provision.getProvisionType() != null)
                    details += provision.getProvisionFamilyType().getLabel() + " - "
                            + provision.getProvisionType().getLabel();
                details += " - ";

                Status status = statusService
                        .getStatus(formaliteGuichetUnique.getStatus().getCode());
                if (status == null)
                    throw new OsirisException(null, "Guichet unique status not found for code "
                            + formaliteGuichetUnique.getStatus().getCode());

                details += "nouveau statut : " + status.getLabel();

                generateNewNotification(employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                        Notification.PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED, provision, details, null, false);
            }
        }
    }
}
