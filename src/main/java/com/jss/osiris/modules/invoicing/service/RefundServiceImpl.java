package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.service.AccountingRecordService;
import com.jss.osiris.modules.invoicing.model.Payment;
import com.jss.osiris.modules.invoicing.model.Refund;
import com.jss.osiris.modules.invoicing.repository.RefundRepository;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.tiers.model.ITiers;
import com.jss.osiris.modules.tiers.model.Tiers;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    AccountingRecordService accountingRecordService;

    @Override
    @Cacheable(value = "refundList", key = "#root.methodName")
    public List<Refund> getRefunds() {
        return IterableUtils.toList(refundRepository.findAll());
    }

    @Override
    @Cacheable(value = "refund", key = "#id")
    public Refund getRefund(Integer id) {
        Optional<Refund> refund = refundRepository.findById(id);
        if (!refund.isEmpty())
            return refund.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "refundList", allEntries = true),
            @CacheEvict(value = "refund", key = "#refund.id")
    })
    public Refund addOrUpdateRefund(
            Refund refund) {
        return refundRepository.save(refund);
    }

    @Override
    public void generateRefund(ITiers tiersRefund, Affaire affaireRefund, Payment payment, Float amount)
            throws Exception {
        Refund refund = new Refund();
        if (tiersRefund instanceof Confrere)
            refund.setConfrere((Confrere) tiersRefund);
        if (tiersRefund instanceof Tiers)
            refund.setTiers((Tiers) tiersRefund);
        if (affaireRefund != null) {
            refund.setAffaire(affaireRefund);
        }
        refund.setLabel("Remboursement du paiement n°" + payment.getId());
        refund.setPayment(payment);
        refund.setRefundAmount(amount);
        refund.setRefundDateTime(LocalDateTime.now());
        this.addOrUpdateRefund(refund);
        // TODO generate refund : IBAN of affaire if not null else confrere else tiers
        accountingRecordService.generateAccountingRecordsForRefund(refund);
    }
}
