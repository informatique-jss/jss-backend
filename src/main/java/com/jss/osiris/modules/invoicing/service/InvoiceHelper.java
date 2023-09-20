package com.jss.osiris.modules.invoicing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IGenericTiers;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class InvoiceHelper {

    @Autowired
    ConstantService constantService;

    public IGenericTiers getCustomerOrder(Invoice invoice) throws OsirisException {
        IGenericTiers customerOrder = null;
        if (invoice.getTiers() != null)
            customerOrder = invoice.getTiers();
        if (invoice.getResponsable() != null)
            customerOrder = invoice.getResponsable().getTiers();
        if (invoice.getProvider() != null)
            customerOrder = invoice.getProvider();
        if (invoice.getConfrere() != null)
            customerOrder = invoice.getConfrere();
        if (invoice.getCompetentAuthority() != null)
            customerOrder = invoice.getCompetentAuthority();

        if (customerOrder == null)
            throw new OsirisException(null, "No customer order declared on Invoice " + invoice.getId());

        return customerOrder;
    }

    public IGenericTiers getCustomerOrder(CustomerOrder inCustomerOrder) throws OsirisException {
        IGenericTiers customerOrder = null;
        if (inCustomerOrder.getTiers() != null)
            customerOrder = inCustomerOrder.getTiers();
        if (inCustomerOrder.getResponsable() != null)
            customerOrder = inCustomerOrder.getResponsable().getTiers();
        if (inCustomerOrder.getConfrere() != null)
            customerOrder = inCustomerOrder.getConfrere();

        if (customerOrder == null)
            throw new OsirisException(null, "No customer order declared on CustomerOrder " + inCustomerOrder.getId());

        return customerOrder;
    }

    public Invoice setPriceTotal(Invoice invoice) {
        if (invoice != null) {
            invoice.setTotalPrice(Math.round(this.getPriceTotal(invoice) * 100f) / 100f);
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

    public Float getTotalForInvoiceItem(InvoiceItem invoiceItem) {
        return (invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f)
                + (invoiceItem.getVatPrice() != null ? invoiceItem.getVatPrice() : 0f)
                - (invoiceItem.getDiscountAmount() != null ? invoiceItem.getDiscountAmount()
                        : 0f);
    }

    public Float getPreTaxPriceTotal(Invoice invoice) {
        Float preTaxTotal = 0f;
        if (invoice != null && invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
            for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
                preTaxTotal += invoiceItem.getPreTaxPrice() != null ? invoiceItem.getPreTaxPrice() : 0f;
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

    public InvoiceLabelResult computeInvoiceLabelResult(Document billingDocument, CustomerOrder customerOrder,
            ITiers orderingCustomer) throws OsirisException {
        InvoiceLabelResult invoiceLabelResult = new InvoiceLabelResult();
        if (billingDocument.getBillingLabelType() == null)
            return invoiceLabelResult;
        // Defined billing label
        if (constantService.getBillingLabelTypeOther().getId().equals(billingDocument.getBillingLabelType().getId())) {
            if (billingDocument.getRegie() != null) {
                invoiceLabelResult.setBillingLabel(billingDocument.getRegie().getLabel());
                invoiceLabelResult.setBillingLabelAddress(billingDocument.getRegie().getAddress());
                invoiceLabelResult.setBillingLabelPostalCode(billingDocument.getRegie().getPostalCode());
                invoiceLabelResult.setCedexComplement(billingDocument.getRegie().getCedexComplement());
                invoiceLabelResult.setBillingLabelCity(billingDocument.getRegie().getCity());
                invoiceLabelResult.setBillingLabelCountry(billingDocument.getRegie().getCountry());
                invoiceLabelResult.setBillingLabelIsIndividual(false);
                invoiceLabelResult.setBillingLabelType(billingDocument.getBillingLabelType());
                invoiceLabelResult.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
                invoiceLabelResult.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
                invoiceLabelResult.setCommandNumber(billingDocument.getCommandNumber());
                invoiceLabelResult.setLabelOrigin("la configuration Facture / Autres du confrère");
            } else {
                invoiceLabelResult.setBillingLabel(billingDocument.getBillingLabel());
                invoiceLabelResult.setBillingLabelAddress(billingDocument.getBillingAddress());
                invoiceLabelResult.setBillingLabelPostalCode(billingDocument.getBillingPostalCode());
                invoiceLabelResult.setCedexComplement(billingDocument.getCedexComplement());
                invoiceLabelResult.setBillingLabelCity(billingDocument.getBillingLabelCity());
                invoiceLabelResult.setBillingLabelCountry(billingDocument.getBillingLabelCountry());
                invoiceLabelResult.setBillingLabelIsIndividual(billingDocument.getBillingLabelIsIndividual());
                invoiceLabelResult.setBillingLabelType(billingDocument.getBillingLabelType());
                invoiceLabelResult.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
                invoiceLabelResult.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
                invoiceLabelResult.setCommandNumber(billingDocument.getCommandNumber());
                invoiceLabelResult.setLabelOrigin("la configuration Facture / Autres du donneur d'ordre");
            }
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
            invoiceLabelResult.setBillingLabelIsIndividual(affaire.getIsIndividual());
            invoiceLabelResult.setBillingLabelType(billingDocument.getBillingLabelType());
            invoiceLabelResult.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
            invoiceLabelResult.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
            invoiceLabelResult.setCommandNumber(billingDocument.getCommandNumber());
            invoiceLabelResult.setLabelOrigin("les informations de l'affaire");

        } else {
            ITiers usedOrderingCustomer = null;

            if (orderingCustomer instanceof Tiers
                    || orderingCustomer instanceof Responsable) {
                Tiers tiers;
                if (orderingCustomer instanceof Tiers) {
                    tiers = (Tiers) orderingCustomer;
                } else {
                    tiers = ((Responsable) orderingCustomer).getTiers();
                }
                invoiceLabelResult.setLabelOrigin("les informations postales du responsable");
                usedOrderingCustomer = tiers;

                String labelResult = tiers.getIsIndividual() ? tiers.getFirstname() + " " + tiers.getLastname()
                        : tiers.getDenomination();

                if (orderingCustomer instanceof Responsable && billingDocument.getIsResponsableOnBilling() != null
                        && billingDocument.getIsResponsableOnBilling()) {
                    labelResult = ((Responsable) orderingCustomer).getFirstname() + " "
                            + ((Responsable) orderingCustomer).getLastname() + " - " + labelResult;
                }

                invoiceLabelResult.setBillingLabel(labelResult);
            } else if (orderingCustomer instanceof Confrere) {
                Confrere confrere = (Confrere) orderingCustomer;
                usedOrderingCustomer = confrere;
                invoiceLabelResult.setBillingLabel(confrere.getLabel());
                invoiceLabelResult.setLabelOrigin("les informations postales du confrère");
            }
            if (usedOrderingCustomer != null) {
                invoiceLabelResult.setBillingLabelAddress(usedOrderingCustomer.getAddress());
                invoiceLabelResult.setBillingLabelPostalCode(usedOrderingCustomer.getPostalCode());
                invoiceLabelResult.setBillingLabelIntercommunityVat(usedOrderingCustomer.getIntercommunityVat());
                invoiceLabelResult.setBillingLabelCity(usedOrderingCustomer.getCity());
                invoiceLabelResult.setBillingLabelCountry(usedOrderingCustomer.getCountry());
                invoiceLabelResult.setCedexComplement(usedOrderingCustomer.getCedexComplement());
                invoiceLabelResult.setBillingLabelIsIndividual(usedOrderingCustomer.getIsIndividual());
                invoiceLabelResult.setBillingLabelType(billingDocument.getBillingLabelType());
                invoiceLabelResult.setIsResponsableOnBilling(billingDocument.getIsResponsableOnBilling());
                invoiceLabelResult.setIsCommandNumberMandatory(billingDocument.getIsCommandNumberMandatory());
                invoiceLabelResult.setCommandNumber(billingDocument.getCommandNumber());
            }
        }
        return invoiceLabelResult;
    }

    public void setInvoiceLabel(Invoice invoice, Document billingDocument, CustomerOrder customerOrder,
            ITiers orderingCustomer) throws OsirisException {
        InvoiceLabelResult invoiceLabelResult = computeInvoiceLabelResult(billingDocument, customerOrder,
                orderingCustomer);
        invoice.setBillingLabel(invoiceLabelResult.getBillingLabel());
        invoice.setBillingLabelAddress(invoiceLabelResult.getBillingLabelAddress());
        invoice.setBillingLabelPostalCode(invoiceLabelResult.getBillingLabelPostalCode());
        invoice.setBillingLabelIntercommunityVat(invoiceLabelResult.getBillingLabelIntercommunityVat());
        invoice.setCedexComplement(invoiceLabelResult.getCedexComplement());
        invoice.setBillingLabelCity(invoiceLabelResult.getBillingLabelCity());
        invoice.setBillingLabelCountry(invoiceLabelResult.getBillingLabelCountry());
        invoice.setBillingLabelIsIndividual(invoiceLabelResult.getBillingLabelIsIndividual());
        invoice.setBillingLabelType(invoiceLabelResult.getBillingLabelType());
        invoice.setIsResponsableOnBilling(invoiceLabelResult.getIsResponsableOnBilling());
        invoice.setIsCommandNumberMandatory(invoiceLabelResult.getIsCommandNumberMandatory());
        invoice.setCommandNumber(invoiceLabelResult.getCommandNumber());
    }

    public String getIbanOfOrderingCustomer(Invoice invoice) {
        if (invoice != null) {
            if (invoice.getTiers() != null)
                return invoice.getTiers().getPaymentIban();
            if (invoice.getResponsable() != null)
                return invoice.getResponsable().getTiers().getPaymentIban();
            if (invoice.getConfrere() != null)
                return invoice.getConfrere().getPaymentIban();
            if (invoice.getProvider() != null)
                return invoice.getProvider().getIban();
            if (invoice.getCompetentAuthority() != null)
                return invoice.getCompetentAuthority().getIban();
        }
        return null;
    }

    public String getBicOfOrderingCustomer(Invoice invoice) {
        if (invoice != null) {
            if (invoice.getTiers() != null)
                return invoice.getTiers().getPaymentBic();
            if (invoice.getResponsable() != null)
                return invoice.getResponsable().getTiers().getPaymentBic();
            if (invoice.getConfrere() != null)
                return invoice.getConfrere().getPaymentBic();
            if (invoice.getProvider() != null)
                return invoice.getProvider().getBic();
            if (invoice.getCompetentAuthority() != null)
                return invoice.getCompetentAuthority().getBic();
        }
        return null;
    }

}
