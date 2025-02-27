package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.miscellaneous.model.InvoicingSummary;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.IOrderingSearchTaggedResult;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearch;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.osiris.quotation.model.OrderingSearchTagged;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.UserCustomerOrder;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.servlet.http.HttpServletRequest;

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

        public String getCardPaymentLinkForCustomerOrderDeposit(CustomerOrder customerOrder, String mail,
                        String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public String getCardPaymentLinkForPaymentInvoice(CustomerOrder customerOrder, String mail, String subject)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public void sendRemindersForCustomerOrderDeposit() throws OsirisException;

        public void sendReminderForCustomerOrderDeposit(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public CustomerOrder unlockCustomerOrderFromDeposit(CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public Boolean validateCardPaymentLinkForCustomerOrder(CustomerOrder customerOrder,
                        com.jss.osiris.modules.osiris.quotation.model.CentralPayPaymentRequest request)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public BigDecimal getRemainingAmountToPayForCustomerOrder(CustomerOrder customerOrder) throws OsirisException;

        public ResponseEntity<byte[]> printMailingLabel(List<String> customerOrders, boolean printLabel,
                        Integer competentAuthorityId,
                        boolean printLetters, boolean printRegisteredLetter)
                        throws OsirisException, OsirisClientMessageException;

        public void updateAssignedToForCustomerOrder(CustomerOrder customerOrder, Employee employee)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<OrderingSearchResult> searchByQuotationId(Integer idQuotation);

        public List<OrderingSearchResult> searchByCustomerOrderParentRecurringId(Integer idCustomerOrder);

        public List<OrderingSearchResult> searchByCustomerOrderParentRecurringByCustomerOrderId(
                        Integer idCustomerOrder);

        public void offerCustomerOrder(CustomerOrder customerOrder) throws OsirisException,
                        OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException;

        public void generateRecurringCustomerOrders() throws OsirisException, OsirisClientMessageException,
                        OsirisValidationException, OsirisDuplicateException;

        public List<CustomerOrder> searchOrdersForCurrentUser(List<String> customerOrderStatus, Integer page,
                        String sortBy);

        public List<CustomerOrder> searchOrdersForCurrentUserAndAffaire(Affaire affaire);

        public List<Payment> getApplicablePaymentsForCustomerOrder(CustomerOrder customerOrder) throws OsirisException;

        public InvoicingSummary getInvoicingSummaryForIQuotation(IQuotation customerOrder) throws OsirisException;

        public List<CustomerOrderComment> getCustomerOrderCommentsForCustomer(CustomerOrder customerOrder);

        public List<CustomerOrder> searchOrders(List<CustomerOrderStatus> customerOrderStatus,
                        List<Responsable> responsables);

        public UserCustomerOrder saveOrderOfUserCustomerOrder(UserCustomerOrder order, HttpServletRequest request)
                        throws OsirisClientMessageException, OsirisValidationException, OsirisException;

        public List<CustomerOrder> completeAdditionnalInformationForCustomerOrders(List<CustomerOrder> customerOrders);

}
