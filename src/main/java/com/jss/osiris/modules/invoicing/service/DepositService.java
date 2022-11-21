package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Deposit;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

public interface DepositService {
        public List<Deposit> getDeposits();

        public Deposit getDeposit(Integer id);

        public Deposit addOrUpdateDeposit(Deposit deposit);

        public Deposit getNewDepositForInvoice(Float depositAmount, LocalDateTime depositDatetime, Invoice invoice,
                        Payment payment) throws OsirisException;

        public Deposit getNewDepositForCustomerOrder(Float depositAmount, LocalDateTime depositDatetime,
                        CustomerOrder customerOrder, Payment payment) throws OsirisException;
}
