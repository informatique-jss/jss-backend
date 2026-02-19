package com.jss.osiris.modules.myjss.quotation.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.quotation.controller.model.DashboardUserStatistics;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationSearch;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderCommentService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class DashboardUserStatisticsServiceImpl implements DashboardUserStatisticsService {

        @Autowired
        EmployeeService employeeService;

        @Autowired
        QuotationService quotationService;

        @Autowired
        CustomerOrderStatusService customerOrderStatusService;

        @Autowired
        QuotationStatusService quotationStatusService;

        @Autowired
        CustomerOrderService customerOrderService;

        @Autowired
        InvoiceService invoiceService;

        @Autowired
        ConstantService constantService;

        @Autowired
        CustomerOrderCommentService customerOrderCommentService;

        @Override
        public DashboardUserStatistics getDashboardUserStatistics() throws OsirisException {
                DashboardUserStatistics statistics = new DashboardUserStatistics();
                List<Responsable> listResponsables = Arrays.asList(employeeService.getCurrentMyJssUser());

                if (listResponsables != null && listResponsables.size() > 0) {

                        // compute customerOrderInProgress
                        statistics.setCustomerOrderInProgress(0);
                        List<CustomerOrder> customerOrderInProgress = customerOrderService.searchOrders(
                                        Arrays.asList(
                                                        customerOrderStatusService
                                                                        .getCustomerOrderStatusByCode(
                                                                                        CustomerOrderStatus.BEING_PROCESSED),
                                                        customerOrderStatusService.getCustomerOrderStatusByCode(
                                                                        CustomerOrderStatus.TO_BILLED)),
                                        false,
                                        listResponsables);
                        if (customerOrderInProgress != null)
                                statistics.setCustomerOrderInProgress(customerOrderInProgress.size());

                        // compute customerOrderRequieringAttention
                        statistics.setCustomerOrderRequieringAttention(0);
                        List<CustomerOrder> customerOrderRequieringAttention = customerOrderService
                                        .searchOrdersForCurrentUser(
                                                        Arrays.asList(CustomerOrderStatus.BEING_PROCESSED,
                                                                        CustomerOrderStatus.WAITING_DEPOSIT,
                                                                        CustomerOrderStatus.BILLED),
                                                        listResponsables.stream().map(respo -> respo.getId()).toList(),
                                                        true, 0, "");

                        statistics.setCustomerOrderRequieringAttention(customerOrderRequieringAttention.size());

                        // compute quotationToValidate
                        statistics.setQuotationToValidate(0);

                        QuotationSearch quotationSearch = new QuotationSearch();
                        quotationSearch.setResponsables(listResponsables);
                        quotationSearch.setQuotationStatus(
                                        Arrays.asList(quotationStatusService
                                                        .getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER)));
                        List<Quotation> quotationToValidate = quotationService.searchForQuotations(quotationSearch);

                        if (quotationToValidate != null)
                                statistics.setQuotationToValidate(quotationToValidate.size());

                        // compute invoiceToPay
                        statistics.setInvoiceToPay(0);
                        List<Invoice> invoiceToPay = invoiceService
                                        .searchInvoices(Arrays.asList(constantService.getInvoiceStatusSend()),
                                                        listResponsables);
                        if (invoiceToPay != null)
                                statistics.setInvoiceToPay(invoiceToPay.size());

                        // compute invoiceAfterDueDate
                        statistics.setInvoiceAfterDueDate(0);
                        if (invoiceToPay != null && invoiceToPay.size() > 0)
                                for (Invoice invoice : invoiceToPay)
                                        if (invoice.getDueDate().isBefore(LocalDate.now()))
                                                statistics.setInvoiceToPay(statistics.getInvoiceToPay() + 1);

                        // compute customerOrderDraft
                        statistics.setCustomerOrderDraft(0);
                        List<CustomerOrder> customerOrderDraft = customerOrderService.searchOrders(
                                        Arrays.asList(customerOrderStatusService
                                                        .getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT)),
                                        false,
                                        listResponsables);
                        if (customerOrderDraft != null)
                                statistics.setCustomerOrderDraft(customerOrderDraft.size());

                        // compute quotationToValidate
                        statistics.setQuotationDraft(0);

                        QuotationSearch quotationSearchDraft = new QuotationSearch();
                        quotationSearchDraft.setResponsables(listResponsables);
                        quotationSearchDraft.setQuotationStatus(
                                        Arrays.asList(quotationStatusService
                                                        .getQuotationStatusByCode(QuotationStatus.DRAFT)));
                        List<Quotation> quotationDraft = quotationService.searchForQuotations(quotationSearchDraft);

                        if (quotationDraft != null)
                                statistics.setQuotationDraft(quotationDraft.size());

                        // compute quotations with unread comments
                        List<Quotation> quotationsWithUnreadComments = customerOrderCommentService
                                        .getQuotationsWithUnreadCommentsForResponsable(listResponsables.get(0));

                        statistics.setUnreadCommentsForQuotations(quotationsWithUnreadComments.size());

                        // compute customer orders with unread comments
                        List<CustomerOrder> ordersWithUnreadComments = customerOrderCommentService
                                        .getCustomerOrdersWithUnreadCommentsForResponsable(listResponsables.get(0));

                        statistics.setUnreadCommentsForCustomerOrders(ordersWithUnreadComments.size());
                }

                return statistics;
        }
}
