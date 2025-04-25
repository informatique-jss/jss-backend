package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.miscellaneous.repository.NotificationRepository;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

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
        Responsable responsable = quotation.getResponsable();

        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService
                .getMyHolidaymaker((Employee) employeeService.getCurrentEmployee());

        if (compareEmployee != null)
            for (Employee employee : compareEmployee)
                if (employee.getId().equals(quotation.getAssignedTo().getId()))
                    createdByMe = true;

        String customerOrderName = "";
        customerOrderName = responsable.getCivility().getLabel() + " "
                + responsable.getFirstname() + " " + responsable.getLastname();

        if (!createdByMe)
            return generateNewNotification(!isFromHuman ? null : (Employee) employeeService.getCurrentEmployee(),
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
        Responsable responsable = customerOrder.getResponsable();

        ArrayList<Notification> notifications = new ArrayList<Notification>();

        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService
                .getMyHolidaymaker((Employee) employeeService.getCurrentEmployee());

        String customerOrderName = "";
        customerOrderName = responsable.getCivility().getLabel() + " "
                + responsable.getFirstname() + " "
                + responsable.getLastname();

        if (notifySalesEmployee) {
            if (compareEmployee != null)
                for (Employee employee : compareEmployee)
                    if (employee.getId().equals(customerOrder.getAssignedTo().getId()))
                        createdByMe = true;

            if (!createdByMe)
                notifications.add(
                        generateNewNotification(
                                isFromHuman && employeeService.getCurrentEmployee() != null
                                        ? (Employee) employeeService.getCurrentEmployee()
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
                                        ? (Employee) employeeService.getCurrentEmployee()
                                        : null,
                                constantService.getEmployeeBillingResponsible(),
                                notificationType, customerOrder, customerOrderName, null, false));
        }

        if (notifyAffaireResponsibles && customerOrder.getAssoAffaireOrders() != null)
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
                                                ? (Employee) employeeService.getCurrentEmployee()
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
            notification.setEmployee((Employee) employeeService.getCurrentEmployee());
            notification.setEntityId(null);
            notification.setEntityType(null);
            if (notification.getIsRead() == null)
                notification.setIsRead(false);
            notification.setCreatedBy((Employee) employeeService.getCurrentEmployee());
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
        List<Employee> compareEmployee = employeeService
                .getMyHolidaymaker((Employee) employeeService.getCurrentEmployee());
        provision = provisionService.getProvision(provision.getId());

        if (!isProvisionClosed(provision) && !isProvisionOpen(provision)) {
            if (compareEmployee != null)
                for (Employee employee : compareEmployee)
                    if (provision.getAssignedTo() != null && employee.getId().equals(provision.getAssignedTo().getId()))
                        createdByMe = true;

            if (!createdByMe && provision.getAssignedTo() != null) {
                String details = "";
                Affaire affaire = provision.getService().getAssoAffaireOrder().getAffaire();
                if (affaire != null)
                    details += affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname());
                details += " - ";
                if (provision.getProvisionFamilyType() != null && provision.getProvisionType() != null)
                    details += provision.getProvisionFamilyType().getLabel() + " - "
                            + provision.getProvisionType().getLabel();
                details += " - ";
                details += attachment.getAttachmentType().getLabel() + " (" + attachment.getDescription() + ")";
                generateNewNotification((Employee) employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                        Notification.PROVISION_ADD_ATTACHMENT, provision, details, null, false);
            }
        }
    }

    @Override
    public void notifyAttachmentAddToService(Service service, Attachment attachment) throws OsirisException {
        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService
                .getMyHolidaymaker((Employee) employeeService.getCurrentEmployee());
        service = serviceService.getService(service.getId());

        if (compareEmployee != null)
            for (Employee employee : compareEmployee)
                if (service.getAssoAffaireOrder().getAssignedTo() != null
                        && employee.getId().equals(service.getAssoAffaireOrder().getAssignedTo().getId()))
                    createdByMe = true;

        if (!createdByMe && service.getAssoAffaireOrder().getAssignedTo() != null && service.getAssoAffaireOrder()
                .getCustomerOrder().getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BEING_PROCESSED)) {
            String details = "";
            Affaire affaire = service.getAssoAffaireOrder().getAffaire();
            if (affaire != null)
                details += affaire.getDenomination() != null ? affaire.getDenomination()
                        : (affaire.getFirstname() + " " + affaire.getLastname());
            details += " - ";
            details += service.getCustomLabel() != null ? service.getCustomLabel()
                    : service.getServiceType().getLabel();
            details += " - ";
            details += attachment.getAttachmentType().getLabel() + " (" + attachment.getDescription() + ")";
            generateNewNotification((Employee) employeeService.getCurrentEmployee(),
                    service.getAssoAffaireOrder().getAssignedTo(),
                    Notification.SERVICE_ADD_ATTACHMENT, service, details, null, false);
        }
    }

    @Override
    public Notification notifyTiersDepositMandatory(Responsable responsable, Invoice invoice)
            throws OsirisException {
        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService
                .getMyHolidaymaker((Employee) employeeService.getCurrentEmployee());
        Employee salesEmployee = null;

        String customerOrderName = "";
        IId entity = responsable;
        customerOrderName = responsable.getCivility().getLabel() + " "
                + responsable.getFirstname() + " "
                + responsable.getLastname();
        customerOrderName += " (" + responsable.getId() + ")";
        salesEmployee = responsable.getSalesEmployee();

        if (salesEmployee != null) {
            if (compareEmployee != null)
                for (Employee employee : compareEmployee)
                    if (employee.getId().equals(salesEmployee.getId()))
                        createdByMe = true;

            if (!createdByMe)
                return generateNewNotification((Employee) employeeService.getCurrentEmployee(), salesEmployee,
                        Notification.TIERS_DEPOSIT_MANDATORY, entity,
                        "Le tiers " + customerOrderName + " a été relancé pour payer la facture " + invoice.getId()
                                + ".",
                        null, false);
        }
        return null;
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
    public void notifyGuichetUniqueFormaliteStatus(Provision provision, FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException {
        provision = provisionService.getProvision(provision.getId());

        if (!isProvisionClosed(provision) && provision.getService().getAssoAffaireOrder() != null
                && provision.getService().getAssoAffaireOrder().getCustomerOrder() != null
                && !provision.getService().getAssoAffaireOrder().getCustomerOrder().getCustomerOrderStatus().getCode()
                        .equals(CustomerOrderStatus.DRAFT)) {

            if (provision.getAssignedTo() != null) {
                String details = "";
                Affaire affaire = provision.getService().getAssoAffaireOrder().getAffaire();
                if (affaire != null)
                    details += affaire.getDenomination() != null ? affaire.getDenomination()
                            : (affaire.getFirstname() + " " + affaire.getLastname());
                details += " - ";
                if (provision.getProvisionFamilyType() != null && provision.getProvisionType() != null)
                    details += provision.getProvisionFamilyType().getLabel() + " - "
                            + provision.getProvisionType().getLabel();
                details += " - ";

                FormaliteGuichetUniqueStatus status = statusService
                        .getFormaliteGuichetUniqueStatus(formaliteGuichetUnique.getStatus().getCode());
                if (status == null)
                    throw new OsirisException(null, "Guichet unique status not found for code "
                            + formaliteGuichetUnique.getStatus().getCode());

                details += "nouveau statut : " + status.getLabel();

                generateNewNotification((Employee) employeeService.getCurrentEmployee(), provision.getAssignedTo(),
                        Notification.PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED, provision, details, null, false);
            }
        }
    }
}
