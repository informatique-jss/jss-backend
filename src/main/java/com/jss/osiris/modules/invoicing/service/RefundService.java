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
import com.jss.osiris.modules.tiers.model.Tiers;

public interface RefundService {
        public List<Refund> getRefunds();

        public Refund getRefund(Integer id);

        public RefundSearchResult getRefundById(Integer id);

        public Refund addOrUpdateRefund(Refund refund) throws OsirisException;

        public List<RefundSearchResult> searchRefunds(RefundSearch refundSearch);

        public void reindexRefunds() throws OsirisException;

        public Refund refundPayment(Tiers tiersRefund, Affaire affaireRefund, Tiers tiersOrder,
                        Payment payment, Float amount, CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public File getRefundExport(RefundSearch refundSearch)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException;
}
