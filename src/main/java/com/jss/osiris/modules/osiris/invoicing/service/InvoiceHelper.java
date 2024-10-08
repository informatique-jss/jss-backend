package com.jss.osiris.modules.osiris.invoicing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class InvoiceHelper {

    @Autowired
    ConstantService constantService;

    public Invoice setPriceTotal(Invoice invoice) {
        if (invoice != null) {
            invoice.setTotalPrice(Math.round(this.getPriceTotal(invoice) * 100.0) / 100.0);
        }
        return invoice;
    }

    public Double getPriceTotal(Invoice invoice) {
        Double total = this.getPreTaxPriceTotal(invoice) - this.getDiscountTotal(invoice) + this.getVatTotal(invoice);
        return total;
    }

    public Double getDiscountTotal(Invoice invoice) {
        Double discountTotal = 0.0;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                if (invoiceItem.getDiscountAmount() != null)
                    discountTotal += invoiceItem.getDiscountAmount();
            }
        }
        return discountTotal;
    }

    public Double getTotalForInvoiceItem(InvoiceItem invoiceItem) {
        return (invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f)
                + (invoiceItem.getVatPrice() != null ? invoiceItem.getVatPrice() : 0f)
                - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount()
                        : 0f);
    }

    public Double getPreTaxPriceTotal(Invoice invoice) {
        Double preTaxTotal = 0.0;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                preTaxTotal += invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f;
            }
        }
        return preTaxTotal;
    }

    public Double getVatTotal(Invoice invoice) {
        Double vatTotal = 0.0;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                if (invoiceItem.getVatPrice() != null)
                    vatTotal += invoiceItem.getVatPrice();
            }
        }
        return vatTotal;
    }

    public InvoiceLabelResult computeInvoiceLabelResult(Document billingDocument, IQuotation customerOrder,
            Responsable orderingCustomer) throws OsirisException {
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
