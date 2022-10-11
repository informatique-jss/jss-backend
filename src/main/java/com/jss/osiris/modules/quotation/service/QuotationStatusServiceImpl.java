package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.QuotationStatus;
import com.jss.osiris.modules.quotation.repository.QuotationStatusRepository;

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

    @Scheduled(initialDelay = 1000, fixedDelay = 1000000000)
    public void updateStatusReferential() {
        updateStatus(QuotationStatus.OPEN, "Ouvert");
        updateStatus(QuotationStatus.TO_VERIFY, "A vérifier");
        updateStatus(QuotationStatus.VALIDATED_BY_JSS, "Validé par JSS");
        updateStatus(QuotationStatus.SENT_TO_CUSTOMER, "Envoyé au client");
        updateStatus(QuotationStatus.VALIDATED_BY_CUSTOMER, "Validé par le client");
        updateStatus(QuotationStatus.REFUSED_BY_CUSTOMER, "Refusé par le client");
        updateStatus(QuotationStatus.ABANDONED, "Abandonné");
        updateStatus(QuotationStatus.BILLED, "Facturée");
        updateStatus(QuotationStatus.CANCELLED, "Annulé");
        updateStatus(QuotationStatus.WAITING_DEPOSIT, "En attente d'acompte");
        updateStatus(QuotationStatus.BEING_PROCESSED, "En cours de traitement");
    }

    private void updateStatus(String code, String label) {
        if (getQuotationStatusByCode(code) == null) {
            QuotationStatus quotationStatus = new QuotationStatus();
            quotationStatus.setCode(code);
            quotationStatus.setLabel(label);
            quotationStatusRepository.save(quotationStatus);
        }
    }

}
