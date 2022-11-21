package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.repository.QuotationStatusRepository;

@Service
public class QuotationStatusServiceImpl implements QuotationStatusService {

    @Autowired
    QuotationStatusRepository quotationStatusRepository;

    @Override
    @Cacheable(value = "quotationStatusList", key = "#root.methodName")
    public List<QuotationStatus> getQuotationStatus() {
        return IterableUtils.toList(quotationStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "quotationStatus", key = "#id")
    public QuotationStatus getQuotationStatus(Integer id) {
        Optional<QuotationStatus> quotationStatus = quotationStatusRepository.findById(id);
        if (quotationStatus.isPresent())
            return quotationStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "quotationStatusList", allEntries = true),
            @CacheEvict(value = "quotationStatus", key = "#bodaccStatus.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public QuotationStatus addOrUpdateQuotationStatus(QuotationStatus quotationStatus) {
        return quotationStatusRepository.save(quotationStatus);
    }

    @Override
    public QuotationStatus getQuotationStatusByCode(String code) {
        return quotationStatusRepository.findByCode(code);
    }

    @Override
    public void updateStatusReferential() throws OsirisException {
        updateStatus(QuotationStatus.OPEN, "Ouvert", "auto_awesome");
        updateStatus(QuotationStatus.TO_VERIFY, "A vérifier", "search");
        updateStatus(QuotationStatus.SENT_TO_CUSTOMER, "Envoyé au client", "outgoing_mail");
        updateStatus(QuotationStatus.VALIDATED_BY_CUSTOMER, "Validé par le client", "approval");
        updateStatus(QuotationStatus.REFUSED_BY_CUSTOMER, "Refusé par le client", "remove_shopping_cart");
        updateStatus(QuotationStatus.ABANDONED, "Abandonné", "block");

        setSuccessor(QuotationStatus.OPEN, QuotationStatus.TO_VERIFY);
        setSuccessor(QuotationStatus.TO_VERIFY, QuotationStatus.SENT_TO_CUSTOMER);
        setSuccessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.VALIDATED_BY_CUSTOMER);
        setSuccessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.REFUSED_BY_CUSTOMER);
        setSuccessor(QuotationStatus.REFUSED_BY_CUSTOMER, QuotationStatus.TO_VERIFY);

        setPredecessor(QuotationStatus.TO_VERIFY, QuotationStatus.OPEN);
        setPredecessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.OPEN);
        setPredecessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.TO_VERIFY);

        // All cancelled
        setSuccessor(QuotationStatus.OPEN, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.TO_VERIFY, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.VALIDATED_BY_CUSTOMER, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.REFUSED_BY_CUSTOMER, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.ABANDONED, QuotationStatus.OPEN);

    }

    protected void updateStatus(String code, String label, String icon) {
        QuotationStatus QuotationStatus = getQuotationStatusByCode(code);
        if (getQuotationStatusByCode(code) == null)
            QuotationStatus = new QuotationStatus();
        QuotationStatus.setPredecessors(null);
        QuotationStatus.setSuccessors(null);
        QuotationStatus.setCode(code);
        QuotationStatus.setLabel(label);
        QuotationStatus.setIcon(icon);
        addOrUpdateQuotationStatus(QuotationStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        QuotationStatus sourceStatus = getQuotationStatusByCode(code);
        QuotationStatus targetStatus = getQuotationStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException("Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<QuotationStatus>());

        for (QuotationStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateQuotationStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws OsirisException {
        QuotationStatus sourceStatus = getQuotationStatusByCode(code);
        QuotationStatus targetStatus = getQuotationStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException("Quotation status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<QuotationStatus>());

        for (QuotationStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateQuotationStatus(sourceStatus);
    }

}
