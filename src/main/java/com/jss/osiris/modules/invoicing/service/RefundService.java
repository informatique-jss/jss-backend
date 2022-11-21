package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.tiers.model.ITiers;

public interface RefundService {
    public List<Refund> getRefunds();

    public Refund getRefund(Integer id);

    public Refund addOrUpdateRefund(Refund refund);

    public void generateRefund(ITiers tiersRefund, Affaire affaireRefund, Payment payment, Float amount)
            throws OsirisException;
}
