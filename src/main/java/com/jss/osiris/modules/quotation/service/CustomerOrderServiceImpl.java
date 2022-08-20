package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Invoice;
import com.jss.osiris.modules.quotation.model.InvoiceItem;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.repository.CustomerOrderRepository;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    @Autowired
    CustomerOrderRepository customerOrderRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Override
    public CustomerOrder getCustomerOrder(Integer id) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(id);
        if (!customerOrder.isEmpty())
            return customerOrder.get();
        return null;
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder) throws Exception {
        if (customerOrder.getId() == null)
            customerOrder.setCreatedDate(LocalDateTime.now());

        customerOrder.setIsQuotation(false);

        // Complete domiciliation end date
        for (Provision provision : customerOrder.getProvisions()) {
            if (provision.getDomiciliation() != null) {
                Domiciliation domiciliation = provision.getDomiciliation();
                if (domiciliation.getEndDate() == null) {
                    domiciliation.setEndDate(domiciliation.getStartDate().plusYears(1));

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getMails() != null
                            && domiciliation.getMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getActivityMails() != null
                            && domiciliation.getActivityMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getActivityMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null
                            && domiciliation.getLegalGardianMails() != null
                            && domiciliation.getLegalGardianMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getLegalGardianMails());

                    if (domiciliation != null
                            && domiciliation.getLegalGardianPhones() != null
                            && domiciliation.getLegalGardianPhones().size() > 0)
                        phoneService.populateMPhoneIds(domiciliation.getLegalGardianPhones());

                }
            }
        }

        // If invoice has not been generated yet, recompute billing items
        if (!hasBeenBilled(customerOrder)) {
            quotationService.getAndSetInvoiceItemsForQuotation(customerOrder);
        }

        customerOrder = customerOrderRepository.save(customerOrder);
        indexEntityService.indexEntity(customerOrder, customerOrder.getId());
        return customerOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerOrder addOrUpdateCustomerOrderStatus(CustomerOrder customerOrder) throws Exception {
        if (customerOrder.getStatus().getCode().equals(QuotationStatus.BILLED)) {
            generateInvoice(customerOrder);
            accountingRecordService.generateAccountingRecordsForSaleOnInvoiceGeneration(
                    getInvoice(customerOrder));
        }
        return this.addOrUpdateCustomerOrder(customerOrder);
    }

    private void generateInvoice(CustomerOrder customerOrder) throws Exception {
        if (!hasAtLeastOneInvoiceItemNotNull(customerOrder))
            throw new Exception("No invoice item found on customer order " + customerOrder.getId());

        // Generate blank invoice
        Invoice invoice = invoiceService.createInvoice(quotationService.getCustomerOrderOfQuotation(customerOrder));
        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());
        // Associate invoice to invoice item
        for (Provision provision : customerOrder.getProvisions()) {
            if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                for (InvoiceItem invoiceItem : provision.getInvoiceItems()) {
                    invoiceItem.setInvoice(invoice);
                    invoice.getInvoiceItems().add(invoiceItem);
                }
        }

        invoice.setCustomerOrder(customerOrder);
        invoiceService.addOrUpdateInvoice(invoice);
    }

    private boolean hasAtLeastOneInvoiceItemNotNull(CustomerOrder customerOrder) {
        if (customerOrder != null && customerOrder.getProvisions() != null && customerOrder.getProvisions().size() > 0)
            for (Provision provision : customerOrder.getProvisions()) {
                if (provision.getInvoiceItems() != null && provision.getInvoiceItems().size() > 0)
                    for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                        if (invoiceItem.getPreTaxPrice() > 0) {
                            return true;
                        }
            }
        return false;
    }

    private boolean hasBeenBilled(CustomerOrder customerOrder) {
        if (customerOrder == null || customerOrder.getProvisions() == null || customerOrder.getProvisions().size() == 0
                || customerOrder.getProvisions().get(0) == null
                || customerOrder.getProvisions().get(0).getInvoiceItems() == null
                || customerOrder.getProvisions().get(0).getInvoiceItems().size() == 0
                || customerOrder.getProvisions().get(0).getInvoiceItems().get(0) == null)
            return false;

        return customerOrder.getProvisions().get(0).getInvoiceItems().get(0).getInvoice() != null;
    }

    /**
     * Assert that provided customerOrder has been billed and throw exception if no
     * invoice is found
     * You can check if customerOrder has been billed with hasBeenBilled method of
     * current class
     * 
     * @param customerOrder
     * @return Invoice
     * @throws Exception
     */
    private Invoice getInvoice(CustomerOrder customerOrder) throws Exception {
        if (customerOrder == null || customerOrder.getProvisions() == null || customerOrder.getProvisions().size() == 0
                || customerOrder.getProvisions().get(0) == null
                || customerOrder.getProvisions().get(0).getInvoiceItems() == null
                || customerOrder.getProvisions().get(0).getInvoiceItems().size() == 0
                || customerOrder.getProvisions().get(0).getInvoiceItems().get(0) == null)
            throw new Exception("No invoice found");

        return customerOrder.getProvisions().get(0).getInvoiceItems().get(0).getInvoice();
    }
}
