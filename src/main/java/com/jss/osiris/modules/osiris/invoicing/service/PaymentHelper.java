package com.jss.osiris.modules.osiris.invoicing.service;

import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.invoicing.model.Payment;

@Service
public class PaymentHelper {

    public Boolean isPaymentAssociatedInOsiris(Payment payment) {
        if (payment.getInvoice() == null && payment.getCustomerOrder() == null
                && payment.getDirectDebitTransfert() == null && payment.getRefund() == null
                && payment.getCompetentAuthority() == null && payment.getProvider() == null
                && payment.getBankTransfert() == null && payment.getAccountingAccount() == null
                && payment.getIsExternallyAssociated() == false && payment.getIsCancelled() == false) {
            return false;
        }
        return true;
    }

}
