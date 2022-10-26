package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Constant;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.miscellaneous.repository.ConstantRepository;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.TiersType;

@Service
public class ConstantServiceImpl implements ConstantService {

    @Autowired
    ConstantRepository constantRepository;

    @Override
    public Constant getConstants() throws Exception {
        List<Constant> constants = IterableUtils.toList(constantRepository.findAll());
        if (constants == null || constants.size() != 1)
            throw new Exception("Constants not defined or multiple");
        return constants.get(0);
    }

    @Override
    public Constant getConstant(Integer id) {
        Optional<Constant> constant = constantRepository.findById(id);
        if (constant.isPresent())
            return constant.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Constant addOrUpdateConstant(
            Constant constant) {
        return constantRepository.save(constant);
    }

    @Override
    public BillingLabelType getBillingLabelTypeCodeAffaire() throws Exception {
        return getConstants().getBillingLabelTypeCodeAffaire();
    }

    @Override
    public BillingLabelType getBillingLabelTypeOther() throws Exception {
        return getConstants().getBillingLabelTypeOther();
    }

    @Override
    public BillingLabelType getBillingLabelTypeCustomer() throws Exception {
        return getConstants().getBillingLabelTypeCustomer();
    }

    @Override
    public AccountingJournal getAccountingJournalSales() throws Exception {
        return getConstants().getAccountingJournalSales();
    }

    @Override
    public AccountingJournal getAccountingJournalPurchases() throws Exception {
        return getConstants().getAccountingJournalPurchases();
    }

    @Override
    public AccountingJournal getAccountingJournalANouveau() throws Exception {
        return getConstants().getAccountingJournalANouveau();
    }

    @Override
    public TiersType getTiersTypeProspect() throws Exception {
        return getConstants().getTiersTypeProspect();
    }

    @Override
    public DocumentType getDocumentTypePublication() throws Exception {
        return getConstants().getDocumentTypePublication();
    }

    @Override
    public DocumentType getDocumentTypeCfe() throws Exception {
        return getConstants().getDocumentTypeCfe();
    }

    @Override
    public DocumentType getDocumentTypeKbis() throws Exception {
        return getConstants().getDocumentTypeKbis();
    }

    @Override
    public DocumentType getDocumentTypeBilling() throws Exception {
        return getConstants().getDocumentTypeBilling();
    }

    @Override
    public DocumentType getDocumentTypeDunning() throws Exception {
        return getConstants().getDocumentTypeDunning();
    }

    @Override
    public DocumentType getDocumentTypeRefund() throws Exception {
        return getConstants().getDocumentTypeRefund();
    }

    @Override
    public DocumentType getDocumentTypeBillingClosure() throws Exception {
        return getConstants().getDocumentTypeBillingClosure();
    }

    @Override
    public DocumentType getDocumentTypeProvisionnalReceipt() throws Exception {
        return getConstants().getDocumentTypeProvisionnalReceipt();
    }

    @Override
    public DocumentType getDocumentTypeProofReading() throws Exception {
        return getConstants().getDocumentTypeProofReading();
    }

    @Override
    public DocumentType getDocumentTypePublicationCertificate() throws Exception {
        return getConstants().getDocumentTypePublicationCertificate();
    }

    @Override
    public DocumentType getDocumentTypeQuotation() throws Exception {
        return getConstants().getDocumentTypeQuotation();
    }

    @Override
    public DocumentType getAttachmentTypeKbis() throws Exception {
        return getConstants().getAttachmentTypeKbis();
    }

    @Override
    public DocumentType getAttachmentTypeCni() throws Exception {
        return getConstants().getAttachmentTypeCni();
    }

    @Override
    public DocumentType getAttachmentTypeLogo() throws Exception {
        return getConstants().getAttachmentTypeLogo();
    }

    @Override
    public DocumentType getAttachmentTypeProofOfAddress() throws Exception {
        return getConstants().getAttachmentTypeProofOfAddress();
    }

    @Override
    public Country getCountryFrance() throws Exception {
        return getConstants().getCountryFrance();
    }

    @Override
    public Country getCountryMonaco() throws Exception {
        return getConstants().getCountryMonaco();
    }

    @Override
    public BillingType getBillingTypeLogo() throws Exception {
        return getConstants().getBillingTypeLogo();
    }

}
