package com.jss.osiris.modules.invoicing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoice;
import com.jss.osiris.modules.invoicing.model.AzureReceiptInvoiceStatus;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.repository.AzureReceiptInvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;

@Service
public class AzureReceiptInvoiceServiceImpl implements AzureReceiptInvoiceService {

    @Autowired
    AzureReceiptInvoiceRepository azureReceiptInvoiceRepository;

    @Autowired
    AzureReceiptService azureReceiptService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AzureInvoiceService azureInvoiceService;

    @Autowired
    ConstantService constantService;

    @Override
    public AzureReceiptInvoice addOrUpdateAzureReceiptInvoice(AzureReceiptInvoice azureReceiptInvoice) {
        return azureReceiptInvoiceRepository.save(azureReceiptInvoice);
    }

    @Override
    public AzureReceiptInvoice markAsReconciliated(AzureReceiptInvoice azureReceiptInvoice, boolean isReconciliated) {
        AzureReceiptInvoice invoice = azureReceiptInvoiceRepository.findById(azureReceiptInvoice.getId()).get();
        invoice.setIsReconciliated(isReconciliated);
        azureReceiptService.checkAllInvoicesReconciliated(invoice.getAzureReceipt());
        return addOrUpdateAzureReceiptInvoice(azureReceiptInvoice);
    }

    @Override
    public AzureReceiptInvoice getAzureReceiptInvoice(Integer id) {
        Optional<AzureReceiptInvoice> invoice = azureReceiptInvoiceRepository.findById(id);
        if (invoice.isPresent())
            return invoice.get();
        return null;
    }

    @Override
    public AzureReceiptInvoiceStatus getAzureReceiptInvoiceStatus(AzureReceiptInvoice azureReceiptInvoice)
            throws OsirisException {

        CompetentAuthority competentAuthority = azureReceiptInvoice.getAzureReceipt().getAttachments().get(0)
                .getCompetentAuthority();
        AzureReceiptInvoiceStatus status = new AzureReceiptInvoiceStatus();

        // Check Osiris invoices
        List<Invoice> invoices = invoiceService.findByCompetentAuthorityAndManualDocumentNumber(competentAuthority,
                azureReceiptInvoice.getInvoiceId());
        if (invoices == null || invoices.size() == 0)
            invoices = invoiceService.findByCompetentAuthorityAndManualDocumentNumberContains(competentAuthority,
                    azureReceiptInvoice.getInvoiceId());

        // Display only invoices with same total
        List<Invoice> finalInvoices = new ArrayList<Invoice>();
        if (invoices != null && invoices.size() > 0)
            for (Invoice invoice : invoices)
                if ((Math.round(invoice.getTotalPrice() * 100f)
                        / 100f) == (Math.round(azureReceiptInvoice.getInvoiceTotal() * 100f) / 100f))
                    finalInvoices.add(invoice);

        status.setInvoices(finalInvoices);
        status.setInvoicesStatus(finalInvoices != null && finalInvoices.size() == 1);

        // Check azure invoices
        List<AzureInvoice> azureInvoices = azureInvoiceService.findByCompetentAuthorityAndInvoiceId(
                competentAuthority,
                azureReceiptInvoice.getInvoiceId());

        if (azureInvoices == null || azureInvoices.size() == 0)
            azureInvoices = azureInvoiceService.findByCompetentAuthorityAndInvoiceIdContains(
                    competentAuthority,
                    azureReceiptInvoice.getInvoiceId());

        // Display only invoices with same total
        List<AzureInvoice> finalAzureInvoices = new ArrayList<AzureInvoice>();
        if (azureInvoices != null && azureInvoices.size() > 0)
            for (AzureInvoice invoice : azureInvoices)
                if (invoice.getIsDisabled() == false && (Math.round(invoice.getInvoiceTotal() * 100f)
                        / 100f) == (Math.round(azureReceiptInvoice.getInvoiceTotal() * 100f) / 100f))
                    finalAzureInvoices.add(invoice);

        status.setAzureInvoices(finalAzureInvoices);
        status.setAzureInvoicesStatus(finalAzureInvoices != null && finalAzureInvoices.size() == 1);

        // Check payment status
        boolean paymentStatus = false;
        if (finalInvoices != null && finalInvoices.size() == 1) {
            Invoice finalInvoice = finalInvoices.get(0);
            if (finalInvoice.getInvoiceStatus().getId().equals(constantService.getInvoiceStatusPayed().getId()))
                paymentStatus = true;

            else if (finalInvoice.getManualPaymentType().getId()
                    .equals(constantService.getPaymentTypeEspeces().getId()))
                paymentStatus = true;

            else if (finalInvoice.getManualPaymentType().getId()
                    .equals(constantService.getPaymentTypeAccount().getId()))
                paymentStatus = true;

            else if (finalInvoice.getManualPaymentType().getId()
                    .equals(constantService.getPaymentTypeVirement().getId()))
                if (finalInvoice.getBankTransfert() != null)
                    if (finalInvoice.getBankTransfert().getIsAlreadyExported() != null
                            && finalInvoice.getBankTransfert().getIsAlreadyExported()
                            || finalInvoice.getBankTransfert().getIsSelectedForExport() != null
                                    && finalInvoice.getBankTransfert().getIsSelectedForExport())
                        paymentStatus = true;
        }

        status.setPaymentStatus(paymentStatus);

        // Check customer invoice sent after AC invoice creation
        boolean customerInvoicedStatus = false;
        if (finalInvoices != null && finalInvoices.size() == 1) {
            if (finalInvoices.get(0).getCustomerOrderForInboundInvoice() != null &&
                    !finalInvoices.get(0).getCustomerOrderForInboundInvoice().getCustomerOrderStatus().getCode()
                            .equals(CustomerOrderStatus.ABANDONED)
                    && !finalInvoices.get(0).getCustomerOrderForInboundInvoice().getCustomerOrderStatus().getCode()
                            .equals(CustomerOrderStatus.BILLED))
                customerInvoicedStatus = true;

            else if (finalInvoices.get(0).getCustomerOrderForInboundInvoice().getCustomerOrderStatus().getCode()
                    .equals(CustomerOrderStatus.BILLED)
                    && finalInvoices.get(0).getCustomerOrderForInboundInvoice().getInvoices() != null) {
                for (Invoice customerInvoice : finalInvoices.get(0).getCustomerOrderForInboundInvoice().getInvoices())
                    if ((customerInvoice.getIsInvoiceFromProvider() == null
                            || customerInvoice.getIsInvoiceFromProvider() == false)
                            && (customerInvoice.getInvoiceStatus()
                                    .equals(constantService.getInvoiceStatusSend())
                                    || customerInvoice.getInvoiceStatus()
                                            .equals(constantService.getInvoiceStatusPayed()))) {
                        if (customerInvoice.getCreatedDate().isAfter(finalInvoices.get(0).getCreatedDate()))
                            customerInvoicedStatus = true;
                    }
            }
        }
        status.setCustomerInvoicedStatus(customerInvoicedStatus);

        return status;
    }

}
