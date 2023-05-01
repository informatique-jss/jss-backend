package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface DepositService {
        public Deposit getDeposit(Integer id);

        public Deposit addOrUpdateDeposit(Deposit deposit);

        public Deposit getNewDepositForInvoice(Float depositAmount, LocalDateTime depositDatetime, Invoice invoice,
                        Integer overrideAccountingOperationId, Payment payment, boolean isFromOriginPayment)
                        throws OsirisException;

        public Deposit getNewDepositForCustomerOrder(Float depositAmount, LocalDateTime depositDatetime,
                        CustomerOrder customerOrder, Integer overrideAccountingOperationId, Payment payment,
                        boolean isFromOriginPayment)
                        throws OsirisException;

        public void moveDepositToInvoice(Deposit deposit, Invoice toInvoice) throws OsirisException;

        public void moveDepositFromInvoiceToCustomerOrder(Deposit deposit, Invoice fromInvoice,
                        CustomerOrder toCustomerOrder) throws OsirisException;

        public void manualMatchDepositInvoicesAndCustomerOrders(Deposit deposit,
                        List<Invoice> correspondingInvoices,
                        List<CustomerOrder> correspondingCustomerOrder, Affaire affaireRefund, ITiers tiersRefund,
                        List<Float> byPassAmount)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;
}
