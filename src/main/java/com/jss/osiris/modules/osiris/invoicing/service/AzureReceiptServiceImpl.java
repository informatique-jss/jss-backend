package com.jss.osiris.modules.osiris.invoicing.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.azure.FormRecognizerService;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceipt;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceiptInvoice;
import com.jss.osiris.modules.osiris.invoicing.model.AzureReceiptInvoiceStatus;
import com.jss.osiris.modules.osiris.invoicing.repository.AzureReceiptRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;

import jakarta.transaction.Transactional;

@Service
public class AzureReceiptServiceImpl implements AzureReceiptService {

    @Autowired
    AzureReceiptRepository azureReceiptRepository;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    FormRecognizerService formRecognizerService;

    @Autowired
    ConstantService constantService;

    @Autowired
    AzureReceiptInvoiceService azureReceiptInvoiceService;

    @Autowired
    BatchService batchService;

    @Override
    public AzureReceipt getAzureReceipt(Integer id) {
        Optional<AzureReceipt> azureReceipt = azureReceiptRepository.findById(id);
        if (azureReceipt.isPresent())
            return azureReceipt.get();
        return null;
    }

    @Override
    public AzureReceipt getAzureReceiptFromUser(Integer id) throws OsirisException {
        AzureReceipt receipt = getAzureReceipt(id);
        if (receipt.getIsReconciliated() == null || receipt.getIsReconciliated() == false) {
            if (receipt.getAzureReceiptInvoices() != null && receipt.getAzureReceiptInvoices().size() > 0) {
                for (AzureReceiptInvoice invoice : receipt.getAzureReceiptInvoices()) {
                    if (invoice.getIsReconciliated() == null || invoice.getIsReconciliated() == false) {
                        AzureReceiptInvoiceStatus status = azureReceiptInvoiceService
                                .getAzureReceiptInvoiceStatus(invoice);
                        if (status.getCustomerInvoicedStatus() && status.getInvoicesStatus()
                                && status.getPaymentStatus())
                            azureReceiptInvoiceService.markAsReconciliated(invoice, true);
                    }
                }
            }
            return getAzureReceipt(id);
        }
        return receipt;
    }

    @Override
    public AzureReceipt addOrUpdateAzureReceipt(AzureReceipt azureReceipt) {
        return azureReceiptRepository.save(azureReceipt);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void analyseReceipt(Attachment attachment) throws OsirisException, OsirisClientMessageException {
        if (attachment != null) {
            try {
                formRecognizerService.recongnizeRecipts(attachment);
            } catch (Exception e) {
                attachmentService.disableAttachment(attachment);
                throw new OsirisException(e,
                        "Erreur while recongnize receipt with Azure fot attachment " + attachment.getId());
            }
        }
    }

    @Override
    public AzureReceipt checkAllInvoicesReconciliated(AzureReceipt azureReceipt) {
        azureReceipt.setIsReconciliated(false);
        if (azureReceipt != null && azureReceipt.getAzureReceiptInvoices() != null) {
            for (AzureReceiptInvoice invoice : azureReceipt.getAzureReceiptInvoices())
                if (invoice.getIsReconciliated() == null || !invoice.getIsReconciliated())
                    return azureReceipt;
            azureReceipt.setIsReconciliated(true);
        }
        return addOrUpdateAzureReceipt(azureReceipt);
    }
}
