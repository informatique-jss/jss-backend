package com.jss.osiris.modules.osiris.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.azure.FormRecognizerService;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.osiris.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.repository.AzureInvoiceRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.VatService;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.service.BankTransfertService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.quotation.service.PricingHelper;
import com.jss.osiris.modules.osiris.quotation.service.ProvisionService;

import jakarta.transaction.Transactional;

@Service
public class AzureInvoiceServiceImpl implements AzureInvoiceService {

    private BigDecimal zeroValue = new BigDecimal(0);
    private BigDecimal oneHundredValue = new BigDecimal(100);

    @Autowired
    AzureInvoiceRepository azureInvoiceRepository;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    FormRecognizerService formRecognizerService;

    @Autowired
    ConstantService constantService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Autowired
    BankTransfertService bankTransfertService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    PricingHelper pricingHelper;

    @Autowired
    VatService vatService;

    @Autowired
    BatchService batchService;

    @Override
    public AzureInvoice getAzureInvoice(Integer id) {
        Optional<AzureInvoice> azureInvoice = azureInvoiceRepository.findById(id);
        if (azureInvoice.isPresent())
            return azureInvoice.get();
        return null;
    }

    @Override
    public AzureInvoice getAzureInvoiceByInvoiceId(String invoiceId) {
        return azureInvoiceRepository.findByInvoiceId(invoiceId);
    }

    @Override
    public List<AzureInvoice> searchAzureInvoicesByInvoiceId(String invoiceId) {
        return azureInvoiceRepository.findByInvoiceIdContainingAndAndInvoice(invoiceId.trim());
    }

    @Override
    public AzureInvoice addOrUpdateAzureInvoice(AzureInvoice azureInvoice) {
        return azureInvoiceRepository.save(azureInvoice);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void analyseInvoice(Attachment attachment)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (attachment != null) {
            try {
                formRecognizerService.recongnizeInvoice(attachment);
            } catch (Exception e) {
                attachmentService.disableAttachment(attachment);
                throw new OsirisException(e,
                        "Erreur while recongnize invoice with Azure for attachment " + attachment.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Invoice generateInvoiceFromAzureInvoice(AzureInvoice azureInvoice, Provision currentProvision)
            throws OsirisClientMessageException, OsirisException, OsirisValidationException {
        azureInvoice = getAzureInvoice(azureInvoice.getId());
        currentProvision = provisionService.getProvision(currentProvision.getId());

        if (azureInvoice.getCompetentAuthority() != null) {
            if (azureInvoice.getCompetentAuthority().getProvider() == null)
                throw new OsirisClientMessageException("Fournisseur non renseigné sur l'autorité compétente");
            if (azureInvoice.getCompetentAuthority().getProvider().getPaymentType() == null)
                throw new OsirisClientMessageException("Type de paiement non renseigné sur l'autorité compétente");
        }

        Invoice invoice = new Invoice();
        if (azureInvoice.getCompetentAuthority() != null)
            invoice.setProvider(azureInvoice.getCompetentAuthority().getProvider());
        invoice.setCustomerOrderForInboundInvoice(
                currentProvision.getService().getAssoAffaireOrder().getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(azureInvoice.getInvoiceId());
        invoice.setIsCreditNote(false);
        invoice.setAzureInvoice(azureInvoice);
        invoice.setManualAccountingDocumentDate(azureInvoice.getInvoiceDate());
        if (azureInvoice.getCompetentAuthority() != null && azureInvoice.getCompetentAuthority().getProvider() != null)
            invoice.setManualPaymentType(azureInvoice.getCompetentAuthority().getProvider().getPaymentType());

        invoice.setInvoiceItems(new ArrayList<InvoiceItem>());

        // Taxable item
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setBillingItem(
                pricingHelper.getAppliableBillingItem(constantService.getBillingTypeEmolumentsDeGreffeDebour(), null));
        invoiceItem.setDiscountAmount(zeroValue);
        invoiceItem.setIsGifted(false);
        invoiceItem.setIsOverridePrice(false);

        invoiceItem.setLabel(
                invoiceItem.getBillingItem().getBillingType().getLabel() + (invoice.getProvider().getLabel()));
        invoiceItem.setPreTaxPrice(azureInvoice.getInvoicePreTaxTotal().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue));
        invoiceItem.setPreTaxPriceReinvoiced(invoiceItem.getPreTaxPrice());
        if (azureInvoice.getCompetentAuthority() != null)
            vatService.completeVatOnInvoiceItem(invoiceItem, invoice);

        invoice.getInvoiceItems().add(invoiceItem);

        // Non taxable item
        InvoiceItem invoiceItem2 = new InvoiceItem();
        invoiceItem2.setBillingItem(
                pricingHelper.getAppliableBillingItem(constantService.getBillingTypeDeboursNonTaxable(), null));
        invoiceItem2.setDiscountAmount(zeroValue);
        invoiceItem2.setIsGifted(false);
        invoiceItem2.setIsOverridePrice(false);
        invoiceItem2.setVat(constantService.getVatZero());

        invoiceItem2.setLabel(invoiceItem2.getBillingItem().getBillingType().getLabel()
                + (invoice.getProvider().getLabel()));
        invoiceItem2.setPreTaxPrice(azureInvoice.getInvoiceNonTaxableTotal().multiply(oneHundredValue)
                .setScale(0, RoundingMode.HALF_EVEN).divide(oneHundredValue));
        invoiceItem2.setPreTaxPriceReinvoiced(invoiceItem2.getPreTaxPrice());
        if (azureInvoice.getCompetentAuthority() != null)
            vatService.completeVatOnInvoiceItem(invoiceItem2, invoice);

        invoice.getInvoiceItems().add(invoiceItem2);

        return invoice;
    }

    @Override
    public List<AzureInvoice> findByCompetentAuthorityAndInvoiceId(CompetentAuthority competentAuthority,
            String invoiceId) {
        return azureInvoiceRepository.findByCompetentAuthorityAndInvoiceId(competentAuthority,
                invoiceId);
    }

    @Override
    public List<AzureInvoice> findByCompetentAuthorityAndInvoiceIdContains(CompetentAuthority competentAuthority,
            String invoiceId) {
        return azureInvoiceRepository.findByCompetentAuthorityAndInvoiceIdContainingIgnoreCase(
                competentAuthority, invoiceId);
    }
}
