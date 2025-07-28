package com.jss.osiris.modules.osiris.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.repository.QuotationStatusRepository;

@Service
public class QuotationStatusServiceImpl implements QuotationStatusService {

    @Autowired
    QuotationStatusRepository quotationStatusRepository;

    @Override
    public List<QuotationStatus> getQuotationStatus() {
        return IterableUtils.toList(quotationStatusRepository.findAll());
    }

    @Override
    public QuotationStatus getQuotationStatus(Integer id) {
        Optional<QuotationStatus> quotationStatus = quotationStatusRepository.findById(id);
        if (quotationStatus.isPresent())
            return quotationStatus.get();
        return null;
    }

    @Override
    public QuotationStatus addOrUpdateQuotationStatus(QuotationStatus quotationStatus) {
        return quotationStatusRepository.save(quotationStatus);
    }

    @Override
    public QuotationStatus getQuotationStatusByCode(String code) {
        return quotationStatusRepository.findByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusReferential() throws OsirisException {
        updateStatus(QuotationStatus.DRAFT, "Brouillon", "auto_awesome");
        updateStatus(QuotationStatus.TO_VERIFY, "A vérifier", "search");
        updateStatus(QuotationStatus.QUOTATION_WAITING_CONFRERE, "En attente du confrère", "supervisor_account");
        updateStatus(QuotationStatus.SENT_TO_CUSTOMER, "Envoyé au client", "outgoing_mail");
        updateStatus(QuotationStatus.VALIDATED_BY_CUSTOMER, "Validé par le client", "approval");
        updateStatus(QuotationStatus.REFUSED_BY_CUSTOMER, "Refusé par le client", "remove_shopping_cart");
        updateStatus(QuotationStatus.ABANDONED, "Abandonné", "block");

        setSuccessor(QuotationStatus.DRAFT, QuotationStatus.TO_VERIFY);
        setSuccessor(QuotationStatus.DRAFT, QuotationStatus.QUOTATION_WAITING_CONFRERE);
        setSuccessor(QuotationStatus.DRAFT, QuotationStatus.SENT_TO_CUSTOMER);
        setSuccessor(QuotationStatus.TO_VERIFY, QuotationStatus.SENT_TO_CUSTOMER);
        setSuccessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.VALIDATED_BY_CUSTOMER);
        setSuccessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.REFUSED_BY_CUSTOMER);
        setSuccessor(QuotationStatus.REFUSED_BY_CUSTOMER, QuotationStatus.DRAFT);

        setPredecessor(QuotationStatus.QUOTATION_WAITING_CONFRERE, QuotationStatus.DRAFT);
        setPredecessor(QuotationStatus.TO_VERIFY, QuotationStatus.DRAFT);
        setPredecessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.TO_VERIFY);

        // All cancelled
        setSuccessor(QuotationStatus.DRAFT, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.TO_VERIFY, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.SENT_TO_CUSTOMER, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.VALIDATED_BY_CUSTOMER, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.REFUSED_BY_CUSTOMER, QuotationStatus.ABANDONED);
        setSuccessor(QuotationStatus.ABANDONED, QuotationStatus.DRAFT);

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
            throw new OsirisException(null, "Status code " + code + " or " + code2 + " do not exist");

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
            throw new OsirisException(null, "Quotation status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<QuotationStatus>());

        for (QuotationStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateQuotationStatus(sourceStatus);
    }

}
