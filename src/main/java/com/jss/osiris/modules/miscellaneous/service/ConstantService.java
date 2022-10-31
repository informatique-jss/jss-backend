package com.jss.osiris.modules.miscellaneous.service;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.invoicing.model.PaymentWay;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.miscellaneous.model.Constant;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.quotation.model.ActType;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.BodaccPublicationType;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.QuotationLabelType;
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;
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

    public AttachmentType getAttachmentTypeKbis() throws Exception;

    public AttachmentType getAttachmentTypeCni() throws Exception;

    public AttachmentType getAttachmentTypeLogo() throws Exception;

    public AttachmentType getAttachmentTypeProofOfAddress() throws Exception;

    public Country getCountryFrance() throws Exception;

    public Country getCountryMonaco() throws Exception;

    public BillingType getBillingTypeLogo() throws Exception;

    public QuotationLabelType getQuotationLabelTypeOther() throws Exception;

    public PaymentType getPaymentTypeCheques() throws Exception;

    public PaymentType getPaymentTypeOther() throws Exception;

    public PaymentType getPaymentTypeVirement() throws Exception;

    public PaymentType getPaymentTypePrelevement() throws Exception;

    public RefundType getRefundTypeVirement() throws Exception;

    public SubscriptionPeriodType getSubscriptionPeriodType12M() throws Exception;

    public LegalForm getLegalFormUnregistered() throws Exception;

    public JournalType getJournalTypeSpel() throws Exception;

    public Confrere getConfrereJss() throws Exception;

    public DomiciliationContractType getDomiciliationContractTypeKeepMail() throws Exception;

    public DomiciliationContractType getDomiciliationContractTypeRouteMail() throws Exception;

    public DomiciliationContractType getDomiciliationContractTypeKeepEmail() throws Exception;

    public DomiciliationContractType getDomiciliationContractTypeRouteEmail() throws Exception;

    public MailRedirectionType getMailRedirectionTypeOther() throws Exception;

    public BodaccPublicationType getBodaccPublicationTypeMerging() throws Exception;

    public BodaccPublicationType getBodaccPublicationTypeSplit() throws Exception;

    public BodaccPublicationType getBodaccPublicationTypePartialSplit() throws Exception;

    public BodaccPublicationType getBodaccPublicationTypePossessionDispatch() throws Exception;

    public BodaccPublicationType getBodaccPublicationTypeEstateRepresentativeDesignation() throws Exception;

    public BodaccPublicationType getBodaccPublicationTypeSaleOfBusiness() throws Exception;

    public ActType getActTypeSeing() throws Exception;

    public ActType getActTypeAuthentic() throws Exception;

    public AssignationType getAssignationTypeEmployee() throws Exception;

    public TransfertFundsType getTransfertFundsTypePhysique() throws Exception;

    public TransfertFundsType getTransfertFundsTypeMoral() throws Exception;

    public TransfertFundsType getTransfertFundsTypeBail() throws Exception;

    public CompetentAuthorityType getCompetentAuthorityTypeRcs() throws Exception;

    public CompetentAuthorityType getCompetentAuthorityTypeCfp() throws Exception;

    public InvoiceStatus getInvoiceStatusSend() throws Exception;

    public InvoiceStatus getInvoiceStatusPayed() throws Exception;

    public PaymentWay getPaymentWayInbound() throws Exception;

    public PaymentWay getPaymentWayOutboud() throws Exception;

    public Vat getVatTwenty() throws Exception;

    public Vat getVatEight() throws Exception;

    public Department getDepartmentMartinique() throws Exception;

    public Department getDepartmentGuadeloupe() throws Exception;

    public Department getDepartmentReunion() throws Exception;

}
