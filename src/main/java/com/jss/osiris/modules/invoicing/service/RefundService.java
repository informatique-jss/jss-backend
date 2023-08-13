package com.jss.osiris.modules.invoicing.service;

import java.io.File;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.model.RefundSearch;
import com.jss.osiris.modules.invoicing.model.RefundSearchResult;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface RefundService {
    public List<Refund> getRefunds();

    public Refund getRefund(Integer id);

    public Refund addOrUpdateRefund(Refund refund);

    public List<RefundSearchResult> searchRefunds(RefundSearch refundSearch);

    public void reindexRefunds();

    public Refund refundPayment(ITiers tiersRefund, Affaire affaireRefund, Payment payment, Float amount,
            CustomerOrder customerOrder)
            throws OsirisException, OsirisClientMessageException;

    public File getRefundExport(RefundSearch refundSearch) throws OsirisException, OsirisValidationException;
}
