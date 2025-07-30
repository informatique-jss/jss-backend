package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.model.Voucher;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoicingBlockage;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AssignationType;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IOrderingSearchTaggedResult;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.InvoicingStatistics;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearch;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchTagged;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface CustomerOrderService {
        public CustomerOrder getCustomerOrder(Integer id);

        public CustomerOrder getCustomerOrderForAnnouncement(Announcement announcement);

        public CustomerOrder checkAllProvisionEnded(CustomerOrder customerOrderIn)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder quotation, boolean isFromUser,
                        boolean checkAllProvisionEnded)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrderFromUser(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void markCustomerOrderAsPayed(CustomerOrder customerOrder, boolean isPayed)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException,
                        OsirisException;

        public void generateCreditNoteForCustomerOrderInvoice(CustomerOrder customerOrder, Invoice invoiceToRefund)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void reinitInvoicing(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder, String targetStatusCode,
                        boolean isFromUser)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder addOrUpdateCustomerOrderStatusFromUser(CustomerOrder customerOrder,
                        String targetStatusCode)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<CustomerOrder> findCustomerOrderByResponsable(Responsable responsable);

        public List<OrderingSearchResult> searchOrders(OrderingSearch orderingSearch);

        public List<IOrderingSearchTaggedResult> searchOrdersTagged(OrderingSearchTagged orderingSearchResult);

        public void reindexCustomerOrder() throws OsirisException;

        public CustomerOrder createNewCustomerOrderFromQuotation(Quotation quotation)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void generateInvoiceMail(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendInvoiceMail(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public String getCardPaymentLinkForCustomerOrderDeposit(List<CustomerOrder> customerOrders, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public String getCardPaymentLinkForPaymentInvoice(List<CustomerOrder> customerOrders, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersForCustomerOrderDeposit() throws OsirisException;

        public void sendReminderForCustomerOrderDeposit(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Boolean validateCardPaymentLinkForCustomerOrder(List<CustomerOrder> customerOrders,
                        com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public BigDecimal getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) throws OsirisException;

        public ResponseEntity<byte[]> printMailingLabel(List<String> customerOrders, boolean printLabel,
                        Integer competentAuthorityId,
                        boolean printLetters, boolean printRegisteredLetter)
                        throws OsirisException, OsirisClientMessageException;

        public List<OrderingSearchResult> searchByQuotationId(Integer idQuotation);

        public List<OrderingSearchResult> searchByCustomerOrderParentRecurringId(Integer idCustomerOrder);

        public List<OrderingSearchResult> searchByCustomerOrderParentRecurringByCustomerOrderId(
                        Integer idCustomerOrder);

        public void offerCustomerOrder(CustomerOrder customerOrder) throws OsirisException,
                        OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException;

        public void generateRecurringCustomerOrders() throws OsirisException, OsirisClientMessageException,
                        OsirisValidationException, OsirisDuplicateException;

        public List<CustomerOrder> searchOrdersForCurrentUser(List<String> customerOrderStatus, Integer page,
                        String sortBy) throws OsirisException;

        public List<CustomerOrder> searchOrdersForCurrentUserAndAffaire(Affaire affaire) throws OsirisException;

        public List<Payment> getApplicablePaymentsForCustomerOrder(CustomerOrder customerOrder) throws OsirisException;

        public InvoicingSummary getInvoicingSummaryForIQuotation(IQuotation customerOrder) throws OsirisException;

        public List<CustomerOrderComment> getCustomerOrderCommentsForCustomer(CustomerOrder customerOrder);

        public List<CustomerOrder> searchOrders(List<CustomerOrderStatus> customerOrderStatus,
                        List<Responsable> responsables);

        public CustomerOrder completeAdditionnalInformationForCustomerOrder(CustomerOrder customerOrder,
                        Boolean populationAssoAffaireOrderTransientField)
                        throws OsirisException;

        public List<CustomerOrder> completeAdditionnalInformationForCustomerOrders(List<CustomerOrder> customerOrders,
                        Boolean populationAssoAffaireOrderTransientField)
                        throws OsirisException;

        public CustomerOrder completeAdditionnalInformationForCustomerOrderWhenIndexing(CustomerOrder customerOrder)
                        throws OsirisException;

        public List<CustomerOrder> searchCustomerOrders(List<Employee> commercials,
                        List<CustomerOrderStatus> status, List<Employee> invoicingEmployees) throws OsirisException;

        public Boolean setEmergencyOnOrder(CustomerOrder customerOrder, Boolean isEnabled)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisException;

        public Boolean setDocumentOnOrder(CustomerOrder customerOrder, Document document)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisException;

        public void assignInvoicingEmployee(CustomerOrder customerOrder, Employee employee)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException,
                        OsirisException;

        public void modifyInvoicingBlockage(CustomerOrder customerOrder, InvoicingBlockage invoicingBlockage)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException,
                        OsirisException;

        public CustomerOrder assignNewCustomerOrderToBilled();

        public InvoicingStatistics getInvoicingStatistics() throws OsirisException;

        public CustomerOrder getCustomerOrderForSubscription(String subscriptionType,
                        Boolean isPriceReductionForSubscription, Integer idArticle) throws OsirisException;

        public boolean isOnlyJssAnnouncement(CustomerOrder customerOrder, Boolean isReadyForBilling)
                        throws OsirisException;

        public void autoBilledProvisions(CustomerOrder recurringCustomerOrder)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException,
                        OsirisException;

        public List<CustomerOrder> getCustomerOrdersByVoucherAndResponsable(Voucher voucher, Responsable responsable);

        public void purgeCustomerOrders() throws OsirisException;

        public Boolean getIsOrderFromQuotation(CustomerOrder customerOrder);

        public List<CustomerOrder> findCustomerOrderByFormalisteAssigned(List<Employee> employees,
                        CustomerOrderStatus customerOrderStatus, Employee assignedUser,
                        AssignationType assignationType);

        public List<CustomerOrder> findCustomerOrderByPubliscisteAssigned(List<Employee> employees,
                        CustomerOrderStatus customerOrderStatus, Employee assignedUser,
                        AssignationType assignationType);

        public List<CustomerOrder> findCustomerOrderByForcedEmployeeAssigned(CustomerOrderStatus customerOrderStatus,
                        Employee assignedUser);

        public void switchResponsable(CustomerOrder order, Responsable responsable);

        public Integer getComplexity(CustomerOrder customerOrder) throws OsirisException;
}
