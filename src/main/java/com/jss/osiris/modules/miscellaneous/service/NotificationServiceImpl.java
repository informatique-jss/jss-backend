package com.jss.osiris.modules.miscellaneous.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.miscellaneous.repository.NotificationRepository;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.service.QuotationService;
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
        notification.setEntityType(entity.getClass().getSimpleName());
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

    private Notification genericNotificationForQuotation(Quotation quotation, String notificationType)
            throws OsirisException {
        ITiers customerOrder = quotationService.getCustomerOrderOfQuotation(quotation);
        Employee salesEmployee = customerOrder.getSalesEmployee();

        // If responsable, try to get Sales Employee of Tiers
        if (customerOrder instanceof Responsable)
            salesEmployee = ((Responsable) customerOrder).getTiers().getSalesEmployee();

        boolean createdByMe = false;
        List<Employee> compareEmployee = employeeService.getMyHolidaymaker(employeeService.getCurrentEmployee());

        for (Employee employee : compareEmployee)
            if (employee.getId().equals(salesEmployee.getId()))
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
            customerOrderName = ((Tiers) customerOrder).getDenomination();

        if (!createdByMe)
            return generateNewNotification(employeeService.getCurrentEmployee(), salesEmployee,
                    notificationType,
                    quotation, customerOrderName, null, false);

        return null;
    }

    @Override
    public Notification notifyNewQuotation(Quotation quotation) throws OsirisException {
        return genericNotificationForQuotation(quotation, Notification.QUOTATION_CREATE);
    }

    @Override
    public Notification notifyQuotationToVerify(Quotation quotation) throws OsirisException {
        return genericNotificationForQuotation(quotation, Notification.QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY);
    }

    @Override
    public Notification notifyQuotationSent(Quotation quotation) throws OsirisException {
        return genericNotificationForQuotation(quotation, Notification.QUOTATION_SENT);
    }

    @Override
    public Notification notifyQuotationValidatedByCustomer(Quotation quotation) throws OsirisException {
        return genericNotificationForQuotation(quotation, Notification.QUOTATION_VALIDATED_BY_CUSOMER);
    }

    @Override
    public Notification notifyQuotationRefusedByCustomer(Quotation quotation) throws OsirisException {
        return genericNotificationForQuotation(quotation, Notification.QUOTATION_REFUSED_BY_CUSOMER);
    }

    private List<Notification> genericNotificationForCustomerOrder(CustomerOrder customerOrder, String notificationType,
            boolean notifyAffaireResponsibles, boolean notifySalesEmployee, boolean notifiyBillingResponsible,
            boolean isFromHuman) throws OsirisException {
        ITiers customerOrderTiers = quotationService.getCustomerOrderOfQuotation(customerOrder);
        Employee salesEmployee = customerOrderTiers.getSalesEmployee();

        ArrayList<Notification> notifications = new ArrayList<Notification>();

        // If responsable, try to get Sales Employee of Tiers
        if (customerOrderTiers instanceof Responsable)
            salesEmployee = ((Responsable) customerOrderTiers).getTiers().getSalesEmployee();

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
            customerOrderName = ((Tiers) customerOrderTiers).getDenomination();

        if (notifySalesEmployee) {
            for (Employee employee : compareEmployee)
                if (employee.getId().equals(salesEmployee.getId()))
                    createdByMe = true;

            if (!createdByMe)
                notifications.add(
                        generateNewNotification(isFromHuman ? employeeService.getCurrentEmployee() : null,
                                salesEmployee,
                                notificationType, customerOrder, customerOrderName, null, false));
        }

        if (notifiyBillingResponsible) {
            for (Employee employee : compareEmployee)
                if (employee.getId().equals(constantService.getEmployeeBillingResponsible().getId()))
                    createdByMe = true;

            if (!createdByMe)
                notifications.add(
                        generateNewNotification(isFromHuman ? employeeService.getCurrentEmployee() : null,
                                constantService.getEmployeeBillingResponsible(),
                                notificationType, customerOrder, customerOrderName, null, false));
        }

        if (notifyAffaireResponsibles)
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                createdByMe = false;
                if (asso.getAssignedTo() != null) {
                    for (Employee employee : compareEmployee)
                        if (employee.getId().equals(asso.getAssignedTo().getId()))
                            createdByMe = true;
                    if (!createdByMe) {
                        notifications.add(
                                generateNewNotification(isFromHuman ? employeeService.getCurrentEmployee() : null,
                                        asso.getAssignedTo(),
                                        notificationType, customerOrder, customerOrderName, null, false));
                        // Do not notify twice
                        compareEmployee.add(asso.getAssignedTo());
                    }
                }
            }
        return notifications;
    }

    @Override
    public List<Notification> notifyNewCustomerOrderQuotation(CustomerOrder customerOrder) throws OsirisException {
        return genericNotificationForCustomerOrder(customerOrder, Notification.CUSTOMER_ORDER_CREATE, false, true,
                false,
                true);
    }

    @Override
    public List<Notification> notifyCustomerOrderToVerify(CustomerOrder customerOrder) throws OsirisException {
        return genericNotificationForCustomerOrder(customerOrder, Notification.CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY,
                true, true, false, true);
    }

    @Override
    public List<Notification> notifyCustomerOrderToBeingProcessed(CustomerOrder customerOrder, boolean isFromHuman)
            throws OsirisException {
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        notifications.addAll(
                genericNotificationForCustomerOrder(customerOrder, Notification.CUSTOMER_ORDER_BEING_PROCESSED, false,
                        true, false, isFromHuman));
        notifications.addAll(genericNotificationForCustomerOrder(customerOrder,
                Notification.CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN, true, false, false, isFromHuman));
        return notifications;
    }

    @Override
    public List<Notification> notifyCustomerOrderToBeingToBilled(CustomerOrder customerOrder) throws OsirisException {
        return genericNotificationForCustomerOrder(customerOrder, Notification.CUSTOMER_ORDER_TO_BE_BILLED,
                false, false, true, true);
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
    public List<Notification> notifyInvoiceToReminder(Invoice invoice) throws OsirisException {
        return genericNotificationForInvoice(invoice, Notification.INVOICE_REMINDER_PAYMENT, true);
    }

    private List<Notification> genericNotificationForInvoice(Invoice invoice, String notificationType,
            boolean notifiyInvoiceReminderResponsible) throws OsirisException {
        ArrayList<Notification> notifications = new ArrayList<Notification>();

        notifications.add(generateNewNotification(null, constantService.getEmployeeInvoiceReminderResponsible(),
                notificationType, invoice, null, null, false));

        return notifications;
    }
}
