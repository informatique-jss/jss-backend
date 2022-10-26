package com.jss.osiris.modules.miscellaneous.service;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.Constant;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.TiersType;

public interface ConstantService {
    public Constant getConstants() throws Exception;

    public Constant getConstant(Integer id);

    public Constant addOrUpdateConstant(Constant constant);

    public BillingLabelType getBillingLabelTypeCodeAffaire() throws Exception;

    public BillingLabelType getBillingLabelTypeOther() throws Exception;

    public BillingLabelType getBillingLabelTypeCustomer() throws Exception;

    public AccountingJournal getAccountingJournalSales() throws Exception;

    public AccountingJournal getAccountingJournalPurchases() throws Exception;

    public AccountingJournal getAccountingJournalANouveau() throws Exception;

    public TiersType getTiersTypeProspect() throws Exception;

    public DocumentType getDocumentTypePublication() throws Exception;

    public DocumentType getDocumentTypeCfe() throws Exception;

    public DocumentType getDocumentTypeKbis() throws Exception;

    public DocumentType getDocumentTypeBilling() throws Exception;

    public DocumentType getDocumentTypeDunning() throws Exception;

    public DocumentType getDocumentTypeRefund() throws Exception;

    public DocumentType getDocumentTypeBillingClosure() throws Exception;

    public DocumentType getDocumentTypeProvisionnalReceipt() throws Exception;

    public DocumentType getDocumentTypeProofReading() throws Exception;

    public DocumentType getDocumentTypePublicationCertificate() throws Exception;

    public DocumentType getDocumentTypeQuotation() throws Exception;

    public DocumentType getAttachmentTypeKbis() throws Exception;

    public DocumentType getAttachmentTypeCni() throws Exception;

    public DocumentType getAttachmentTypeLogo() throws Exception;

    public DocumentType getAttachmentTypeProofOfAddress() throws Exception;

    public Country getCountryFrance() throws Exception;

    public Country getCountryMonaco() throws Exception;

    public BillingType getBillingTypeLogo() throws Exception;

}
