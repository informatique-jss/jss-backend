package com.jss.osiris.modules.osiris.invoicing.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@Service
public class InvoiceHelper {

    @Autowired
    ConstantService constantService;

    @Autowired
    DocumentService documentService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;
    BigDecimal oneHundredConstant = new BigDecimal(100);

    public Invoice setPriceTotal(Invoice invoice) {
        if (invoice != null) {
            invoice.setTotalPrice(
                    (this.getPriceTotal(invoice).multiply(oneHundredConstant)).setScale(0).divide(oneHundredConstant));
        }
        return invoice;
    }

    public BigDecimal getPriceTotal(Invoice invoice) {
        BigDecimal total = this.getPreTaxPriceTotal(invoice).subtract(this.getDiscountTotal(invoice))
                .add(this.getVatTotal(invoice));
        return total;
    }

    public BigDecimal getDiscountTotal(Invoice invoice) {
        BigDecimal discountTotal = BigDecimal.ZERO;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                if (invoiceItem.getDiscountAmount() != null)
                    discountTotal = discountTotal.add(invoiceItem.getDiscountAmount());
            }
        }
        return discountTotal;
    }

    public BigDecimal getTotalForInvoiceItem(InvoiceItem invoiceItem) {
        return (invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : BigDecimal.ZERO)
                .add((invoiceItem.getVatPrice() != null ? invoiceItem.getVatPrice() : BigDecimal.ZERO))
                .subtract(invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount()
                        : BigDecimal.ZERO);
    }

    public BigDecimal getPreTaxPriceTotal(Invoice invoice) {
        BigDecimal preTaxTotal = BigDecimal.ZERO;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                preTaxTotal = preTaxTotal
                        .add(invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : BigDecimal.ZERO);
            }
        }
        return preTaxTotal;
    }

    public BigDecimal getVatTotal(Invoice invoice) {
        BigDecimal vatTotal = BigDecimal.ZERO;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                if (invoiceItem.getVatPrice() != null)
                    vatTotal = vatTotal.add(invoiceItem.getVatPrice());
            }
        }
        return vatTotal;
    }

    @Transactional(rollbackFor = Exception.class)
    public InvoiceLabelResult computeInvoiceLabelResult(Document billingDocument, IQuotation customerOrder,
            Responsable orderingCustomer) throws OsirisException {

        if (billingDocument.getId() != null)
            billingDocument = documentService.getDocument(billingDocument.getId());

        orderingCustomer = responsableService.getResponsable(orderingCustomer.getId());

        if (customerOrder.getId() != null) {
            if (customerOrder instanceof CustomerOrder)
                customerOrder = customerOrderService.getCustomerOrder(customerOrder.getId());
            else
                customerOrder = quotationService.getQuotation(customerOrder.getId());
        }

        InvoiceLabelResult invoiceLabelResult = new InvoiceLabelResult();
        if (billingDocument.getBillingLabelType() == null)
            return invoiceLabelResult;
        // Defined billing label
        if (constantService.getBillingLabelTypeOther().getId().equals(billingDocument.getBillingLabelType().getId())) {
            invoiceLabelResult.setBillingLabel(billingDocument.getBillingLabel());
            invoiceLabelResult.setBillingLabelAddress(billingDocument.getBillingAddress());
            invoiceLabelResult.setBillingLabelPostalCode(billingDocument.getBillingPostalCode());
            invoiceLabelResult.setCedexComplement(billingDocument.getCedexComplement());
            invoiceLabelResult.setBillingLabelCity(billingDocument.getBillingLabelCity());
            invoiceLabelResult.setBillingLabelCountry(billingDocument.getBillingLabelCountry());
            invoiceLabelResult.setBillingLabelType(billingDocument.getBillingLabelType());
            invoiceLabelResult.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoiceLabelResult.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoiceLabelResult.setCommandNumber(billingDocument.getCommandNumber());
            invoiceLabelResult.setLabelOrigin("la configuration Facture / Autres du donneur d'ordre");
        } else if (customerOrder != null
                && constantService.getBillingLabelTypeCodeAffaire().getId()
                        .equals(billingDocument.getBillingLabelType().getId())) {
            invoiceLabelResult.setLabelOrigin("les informations de l'affaire");
            if (customerOrder.getAssoAffaireOrders() == null || customerOrder.getAssoAffaireOrders().size() == 0)
                return invoiceLabelResult;
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            invoiceLabelResult
                    .setBillingLabel(affaire.getIsIndividual() ? affaire.getFirstname() + " " + affaire.getLastname()
                            : affaire.getDenomination());
            invoiceLabelResult.setBillingLabelAddress(affaire.getAddress());
            invoiceLabelResult.setBillingLabelPostalCode(affaire.getPostalCode());
            invoiceLabelResult.setCedexComplement(affaire.getCedexComplement());
            invoiceLabelResult.setBillingLabelCity(affaire.getCity());
            invoiceLabelResult.setBillingLabelIntercommunityVat(affaire.getIntercommunityVat());
            invoiceLabelResult.setBillingLabelCountry(affaire.getCountry());
            invoiceLabelResult.setBillingLabelType(billingDocument.getBillingLabelType());
            invoiceLabelResult.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoiceLabelResult.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoiceLabelResult.setCommandNumber(billingDocument.getCommandNumber());
            invoiceLabelResult.setLabelOrigin("les informations de l'affaire");

        } else {
            invoiceLabelResult.setLabelOrigin("les informations postales du responsable");
            String labelResult = orderingCustomer.getTiers().getIsIndividual()
                    ? orderingCustomer.getTiers().getFirstname() + " " + orderingCustomer.getTiers().getLastname()
                    : orderingCustomer.getTiers().getDenomination();

            if (billingDocument.getIsResponsableOnBilling() != null
                    && billingDocument.getIsResponsableOnBilling()) {
                labelResult = ((Responsable) orderingCustomer).getFirstname() + " "
                        + ((Responsable) orderingCustomer).getLastname() + " - " + labelResult;
            }

            invoiceLabelResult.setBillingLabel(labelResult);

            invoiceLabelResult.setBillingLabelAddress(orderingCustomer.getAddress());
            invoiceLabelResult.setBillingLabelPostalCode(orderingCustomer.getPostalCode());
            invoiceLabelResult.setBillingLabelIntercommunityVat(orderingCustomer.getIntercommunityVat());
            invoiceLabelResult.setBillingLabelCity(orderingCustomer.getCity());
            invoiceLabelResult.setBillingLabelCountry(orderingCustomer.getCountry());
            invoiceLabelResult.setCedexComplement(orderingCustomer.getCedexComplement());
            invoiceLabelResult.setBillingLabelType(billingDocument.getBillingLabelType());
            invoiceLabelResult.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoiceLabelResult.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoiceLabelResult.setCommandNumber(billingDocument.getCommandNumber());
        }
        return invoiceLabelResult;
    }

    public void setInvoiceLabel(Invoice invoice, Document billingDocument, CustomerOrder customerOrder,
            Responsable orderingCustomer) throws OsirisException {
        InvoiceLabelResult invoiceLabelResult = computeInvoiceLabelResult(billingDocument, customerOrder,
                orderingCustomer);
        invoice.setBillingLabel(invoiceLabelResult.getBillingLabel());
        invoice.setBillingLabelAddress(invoiceLabelResult.getBillingLabelAddress());
        invoice.setBillingLabelPostalCode(invoiceLabelResult.getBillingLabelPostalCode());
        invoice.setBillingLabelIntercommunityVat(invoiceLabelResult.getBillingLabelIntercommunityVat());
        invoice.setCedexComplement(invoiceLabelResult.getCedexComplement());
        invoice.setBillingLabelCity(invoiceLabelResult.getBillingLabelCity());
        invoice.setBillingLabelCountry(invoiceLabelResult.getBillingLabelCountry());
        invoice.setBillingLabelType(invoiceLabelResult.getBillingLabelType());
        invoice.setIsResponsableOnBilling(invoiceLabelResult.getIsResponsableOnBilling());
        invoice.setIsCommandNumberMandatory(invoiceLabelResult.getIsCommandNumberMandatory());
        invoice.setCommandNumber(invoiceLabelResult.getCommandNumber());
    }
}
