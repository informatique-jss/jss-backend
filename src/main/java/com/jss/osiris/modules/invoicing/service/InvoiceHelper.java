package com.jss.osiris.modules.invoicing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class InvoiceHelper {

    @Autowired
    ConstantService constantService;

    public ITiers getCustomerOrder(Invoice invoice) throws Exception {
        if (invoice.getConfrere() != null)
            return invoice.getConfrere();

        if (invoice.getResponsable() != null)
            return invoice.getResponsable();

        if (invoice.getTiers() != null)
            return invoice.getTiers();

        throw new Exception("No customer order declared on Invoice " + invoice.getId());
    }

    public Invoice setPriceTotal(Invoice invoice) {
        if (invoice != null) {
            invoice.setTotalPrice(this.getPriceTotal(invoice));
        }
        return invoice;
    }

    public Float getPriceTotal(Invoice invoice) {
        Float total = this.getPreTaxPriceTotal(invoice) - this.getDiscountTotal(invoice) + this.getVatTotal(invoice);
        return total;
    }

    public Float getDiscountTotal(Invoice invoice) {
        Float discountTotal = 0f;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                if (invoiceItem.getDiscountAmount() != null)
                    discountTotal += invoiceItem.getDiscountAmount();
            }
        }
        return discountTotal;
    }

    public Float getPreTaxPriceTotal(Invoice invoice) {
        Float preTaxTotal = 0f;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                preTaxTotal += invoiceItem.getPreTaxPrice();
            }
        }
        return preTaxTotal;
    }

    public Float getVatTotal(Invoice invoice) {
        Float vatTotal = 0f;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                if (invoiceItem.getVatPrice() != null)
                    vatTotal += invoiceItem.getVatPrice();
            }
        }
        return vatTotal;
    }

    public void setInvoiceLabel(Invoice invoice, Document billingDocument, CustomerOrder customerOrder,
            ITiers orderingCustomer) throws Exception {
        // Defined billing label
        if (constantService.getBillingLabelTypeOther().getId().equals(billingDocument.getBillingLabelType().getId())) {
            if (billingDocument.getRegie() != null) {
                invoice.setBillingLabel(billingDocument.getRegie().getLabel());
                invoice.setBillingLabelAddress(billingDocument.getRegie().getAddress());
                invoice.setBillingLabelPostalCode(billingDocument.getRegie().getPostalCode());
                invoice.setBillingLabelCity(billingDocument.getRegie().getCity());
                invoice.setBillingLabelCountry(billingDocument.getRegie().getCountry());
                invoice.setBillingLabelIsIndividual(false);
                invoice.setBillingLabelType(billingDocument.getBillingLabelType());
                invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
                invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
                invoice.setCommandNumber(billingDocument.getCommandNumber());
            } else {
                invoice.setBillingLabel(billingDocument.getBillingLabel());
                invoice.setBillingLabelAddress(billingDocument.getBillingAddress());
                invoice.setBillingLabelPostalCode(billingDocument.getBillingPostalCode());
                invoice.setBillingLabelCity(billingDocument.getBillingLabelCity());
                invoice.setBillingLabelCountry(billingDocument.getBillingLabelCountry());
                invoice.setBillingLabelIsIndividual(billingDocument.getBillingLabelIsIndividual());
                invoice.setBillingLabelType(billingDocument.getBillingLabelType());
                invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
                invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
                invoice.setCommandNumber(billingDocument.getCommandNumber());
            }
        } else if (customerOrder != null
                && constantService.getBillingLabelTypeCodeAffaire().getId()
                        .equals(billingDocument.getBillingLabelType().getId())) {
            if (customerOrder.getAssoAffaireOrders() == null || customerOrder.getAssoAffaireOrders().size() == 0)
                throw new Exception("No affaire in the customer order " + customerOrder.getId());
            Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
            invoice.setBillingLabel(affaire.getIsIndividual() ? affaire.getFirstname() + " " + affaire.getLastname()
                    : affaire.getDenomination());
            invoice.setBillingLabelAddress(affaire.getAddress());
            invoice.setBillingLabelPostalCode(affaire.getPostalCode());
            invoice.setBillingLabelCity(affaire.getCity());
            invoice.setBillingLabelCountry(affaire.getCountry());
            invoice.setBillingLabelIsIndividual(affaire.getIsIndividual());
            invoice.setBillingLabelType(billingDocument.getBillingLabelType());
            invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoice.setCommandNumber(billingDocument.getCommandNumber());
        } else {
            if (invoice.getTiers() != null)
                invoice.setBillingLabel(invoice.getTiers().getIsIndividual()
                        ? invoice.getTiers().getFirstname() + " " + invoice.getTiers().getLastname()
                        : invoice.getTiers().getDenomination());
            if (invoice.getResponsable() != null)
                invoice.setBillingLabel(
                        invoice.getResponsable().getFirstname() + " " + invoice.getResponsable().getLastname());
            if (invoice.getConfrere() != null)
                invoice.setBillingLabel(invoice.getConfrere().getLabel());
            invoice.setBillingLabelAddress(orderingCustomer.getAddress());
            invoice.setBillingLabelPostalCode(orderingCustomer.getPostalCode());
            invoice.setBillingLabelCity(orderingCustomer.getCity());
            invoice.setBillingLabelCountry(orderingCustomer.getCountry());
            invoice.setBillingLabelIsIndividual(orderingCustomer.getIsIndividual());
            invoice.setBillingLabelType(billingDocument.getBillingLabelType());
            invoice.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoice.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoice.setCommandNumber(billingDocument.getCommandNumber());
        }
    }

}
