package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.jss.osiris.modules.miscellaneous.repository.ConstantRepository;
import com.jss.osiris.modules.quotation.model.ActType;
import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.model.BodaccPublicationType;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.quotation.model.JournalType;
import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.model.QuotationLabelType;
import com.jss.osiris.modules.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.RefundType;
import com.jss.osiris.modules.tiers.model.SubscriptionPeriodType;
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
    public AttachmentType getAttachmentTypeKbis() throws Exception {
        return getConstants().getAttachmentTypeKbis();
    }

    @Override
    public AttachmentType getAttachmentTypeCni() throws Exception {
        return getConstants().getAttachmentTypeCni();
    }

    @Override
    public AttachmentType getAttachmentTypeLogo() throws Exception {
        return getConstants().getAttachmentTypeLogo();
    }

    @Override
    public AttachmentType getAttachmentTypeProofOfAddress() throws Exception {
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

    @Override
    public QuotationLabelType getQuotationLabelTypeOther() throws Exception {
        return getConstants().getQuotationLabelTypeOther();
    }

    @Override
    public PaymentType getPaymentTypeEspeces() throws Exception {
        return getConstants().getPaymentTypeEspeces();
    }

    @Override
    public PaymentType getPaymentTypeCB() throws Exception {
        return getConstants().getPaymentTypeCB();
    }

    @Override
    public PaymentType getPaymentTypeVirement() throws Exception {
        return getConstants().getPaymentTypeVirement();
    }

    @Override
    public PaymentType getPaymentTypePrelevement() throws Exception {
        return getConstants().getPaymentTypePrelevement();
    }

    @Override
    public RefundType getRefundTypeVirement() throws Exception {
        return getConstants().getRefundTypeVirement();
    }

    @Override
    public SubscriptionPeriodType getSubscriptionPeriodType12M() throws Exception {
        return getConstants().getSubscriptionPeriodType12M();
    }

    @Override
    public LegalForm getLegalFormUnregistered() throws Exception {
        return getConstants().getLegalFormUnregistered();
    }

    @Override
    public JournalType getJournalTypeSpel() throws Exception {
        return getConstants().getJournalTypeSpel();
    }

    @Override
    public Confrere getConfrereJss() throws Exception {
        return getConstants().getConfrereJss();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeKeepMail() throws Exception {
        return getConstants().getDomiciliationContractTypeKeepMail();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeRouteMail() throws Exception {
        return getConstants().getDomiciliationContractTypeRouteMail();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeRouteEmailAndMail() throws Exception {
        return getConstants().getDomiciliationContractTypeRouteEmailAndMail();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeRouteEmail() throws Exception {
        return getConstants().getDomiciliationContractTypeRouteEmail();
    }

    @Override
    public MailRedirectionType getMailRedirectionTypeOther() throws Exception {
        return getConstants().getMailRedirectionTypeOther();
    }

    @Override
    public BodaccPublicationType getBodaccPublicationTypeMerging() throws Exception {
        return getConstants().getBodaccPublicationTypeMerging();
    }

    @Override
    public BodaccPublicationType getBodaccPublicationTypeSplit() throws Exception {
        return getConstants().getBodaccPublicationTypeSplit();
    }

    @Override
    public BodaccPublicationType getBodaccPublicationTypePartialSplit() throws Exception {
        return getConstants().getBodaccPublicationTypePartialSplit();
    }

    @Override
    public BodaccPublicationType getBodaccPublicationTypePossessionDispatch() throws Exception {
        return getConstants().getBodaccPublicationTypePossessionDispatch();
    }

    @Override
    public BodaccPublicationType getBodaccPublicationTypeEstateRepresentativeDesignation() throws Exception {
        return getConstants().getBodaccPublicationTypeEstateRepresentativeDesignation();
    }

    @Override
    public BodaccPublicationType getBodaccPublicationTypeSaleOfBusiness() throws Exception {
        return getConstants().getBodaccPublicationTypeSaleOfBusiness();
    }

    @Override
    public ActType getActTypeSeing() throws Exception {
        return getConstants().getActTypeSeing();
    }

    @Override
    public ActType getActTypeAuthentic() throws Exception {
        return getConstants().getActTypeAuthentic();
    }

    @Override
    public AssignationType getAssignationTypeEmployee() throws Exception {
        return getConstants().getAssignationTypeEmployee();
    }

    @Override
    public TransfertFundsType getTransfertFundsTypePhysique() throws Exception {
        return getConstants().getTransfertFundsTypePhysique();
    }

    @Override
    public TransfertFundsType getTransfertFundsTypeMoral() throws Exception {
        return getConstants().getTransfertFundsTypeMoral();
    }

    @Override
    public TransfertFundsType getTransfertFundsTypeBail() throws Exception {
        return getConstants().getTransfertFundsTypeBail();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeRcs() throws Exception {
        return getConstants().getCompetentAuthorityTypeRcs();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeCfp() throws Exception {
        return getConstants().getCompetentAuthorityTypeCfp();
    }

    @Override
    public InvoiceStatus getInvoiceStatusSend() throws Exception {
        return getConstants().getInvoiceStatusSend();
    }

    @Override
    public InvoiceStatus getInvoiceStatusPayed() throws Exception {
        return getConstants().getInvoiceStatusPayed();
    }

    @Override
    public InvoiceStatus getInvoiceStatusCancelled() throws Exception {
        return getConstants().getInvoiceStatusCancelled();
    }

    @Override
    public PaymentWay getPaymentWayInbound() throws Exception {
        return getConstants().getPaymentWayInbound();
    }

    @Override
    public PaymentWay getPaymentWayOutboud() throws Exception {
        return getConstants().getPaymentWayOutboud();
    }

    @Override
    public Vat getVatTwenty() throws Exception {
        return getConstants().getVatTwenty();
    }

    @Override
    public Vat getVatEight() throws Exception {
        return getConstants().getVatEight();
    }

    @Override
    public Department getDepartmentMartinique() throws Exception {
        return getConstants().getDepartmentMartinique();
    }

    @Override
    public Department getDepartmentGuadeloupe() throws Exception {
        return getConstants().getDepartmentGuadeloupe();
    }

    @Override
    public Department getDepartmentReunion() throws Exception {
        return getConstants().getDepartmentReunion();
    }

    @Override
    public TypePersonne getTypePersonnePersonnePhysique() throws Exception {
        return getConstants().getTypePersonnePersonnePhysique();
    }

    @Override
    public TypePersonne getTypePersonnePersonneMorale() throws Exception {
        return getConstants().getTypePersonnePersonneMorale();
    }

    @Override
    public TypePersonne getTypePersonneExploitation() throws Exception {
        return getConstants().getTypePersonneExploitation();
    }

    @Override
    public FormeJuridique getFormeJuridiqueEntrepreneurIndividuel() throws Exception {
        return getConstants().getFormeJuridiqueEntrepreneurIndividuel();
    }

    @Override
    public TypeFormalite getTypeFormaliteCessation() throws Exception {
        return getConstants().getTypeFormaliteCessation();
    }

    @Override
    public TypeFormalite getTypeFormaliteModification() throws Exception {
        return getConstants().getTypeFormaliteModification();
    }

    @Override
    public TypeFormalite getTypeFormaliteCreation() throws Exception {
        return getConstants().getTypeFormaliteCreation();
    }

    @Override
    public TypeFormalite getTypeFormaliteCorrection() throws Exception {
        return getConstants().getTypeFormaliteCorrection();
    }
}
