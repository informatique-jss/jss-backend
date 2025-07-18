package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;
import com.jss.osiris.modules.osiris.accounting.model.AccountingAccountClass;
import com.jss.osiris.modules.osiris.accounting.model.AccountingJournal;
import com.jss.osiris.modules.osiris.accounting.model.PrincipalAccountingAccount;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.BillingType;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Constant;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderFrequency;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.DeliveryService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.model.DocumentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Language;
import com.jss.osiris.modules.osiris.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaymentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.Provider;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.miscellaneous.model.Vat;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.ActType;
import com.jss.osiris.modules.osiris.quotation.model.AssignationType;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationContractType;
import com.jss.osiris.modules.osiris.quotation.model.JournalType;
import com.jss.osiris.modules.osiris.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionFamilyType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.osiris.quotation.model.ProvisionType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;
import com.jss.osiris.modules.osiris.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypePersonne;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureRecipientType;
import com.jss.osiris.modules.osiris.tiers.model.BillingClosureType;
import com.jss.osiris.modules.osiris.tiers.model.BillingLabelType;
import com.jss.osiris.modules.osiris.tiers.model.PaymentDeadlineType;
import com.jss.osiris.modules.osiris.tiers.model.RefundType;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.RffFrequency;
import com.jss.osiris.modules.osiris.tiers.model.SubscriptionPeriodType;
import com.jss.osiris.modules.osiris.tiers.model.TiersCategory;
import com.jss.osiris.modules.osiris.tiers.model.TiersFollowupType;
import com.jss.osiris.modules.osiris.tiers.model.TiersType;

@Service
public class ConstantServiceImpl implements ConstantService {

    @Autowired
    ConstantServiceProxyImpl constantServiceProxy;

    @Override
    public Constant addOrUpdateConstant(
            Constant constant) throws OsirisException {
        return constantServiceProxy.addOrUpdateConstant(constant);
    }

    @Override
    public Constant getConstants() throws OsirisException {
        return constantServiceProxy.getConstants();
    }

    @Override
    public BillingLabelType getBillingLabelTypeCodeAffaire() throws OsirisException {
        return getConstants().getBillingLabelTypeCodeAffaire();
    }

    @Override
    public BillingLabelType getBillingLabelTypeOther() throws OsirisException {
        return getConstants().getBillingLabelTypeOther();
    }

    @Override
    public BillingLabelType getBillingLabelTypeCustomer() throws OsirisException {
        return getConstants().getBillingLabelTypeCustomer();
    }

    @Override
    public AccountingJournal getAccountingJournalSales() throws OsirisException {
        return getConstants().getAccountingJournalSales();
    }

    @Override
    public AccountingJournal getAccountingJournalSituation() throws OsirisException {
        return getConstants().getAccountingJournalSituation();
    }

    @Override
    public AccountingJournal getAccountingJournalSalary() throws OsirisException {
        return getConstants().getAccountingJournalSalary();
    }

    @Override
    public AccountingJournal getAccountingJournalPurchases() throws OsirisException {
        return getConstants().getAccountingJournalPurchases();
    }

    @Override
    public AccountingJournal getAccountingJournalMiscellaneousOperations() throws OsirisException {
        return getConstants().getAccountingJournalMiscellaneousOperations();
    }

    @Override
    public AccountingJournal getAccountingJournalANouveau() throws OsirisException {
        return getConstants().getAccountingJournalANouveau();
    }

    @Override
    public AccountingJournal getAccountingJournalBank() throws OsirisException {
        return getConstants().getAccountingJournalBank();
    }

    @Override
    public AccountingJournal getAccountingJournalCash() throws OsirisException {
        return getConstants().getAccountingJournalCash();
    }

    @Override
    public AccountingJournal getAccountingJournalBilan() throws OsirisException {
        return getConstants().getAccountingJournalBilan();
    }

    @Override
    public TiersType getTiersTypeProspect() throws OsirisException {
        return getConstants().getTiersTypeProspect();
    }

    @Override
    public TiersType getTiersTypeClient() throws OsirisException {
        return getConstants().getTiersTypeClient();
    }

    @Override
    public DocumentType getDocumentTypeDigital() throws OsirisException {
        return getConstants().getDocumentTypeDigital();
    }

    @Override
    public DocumentType getDocumentTypePaper() throws OsirisException {
        return getConstants().getDocumentTypePaper();
    }

    @Override
    public DocumentType getDocumentTypeBilling() throws OsirisException {
        return getConstants().getDocumentTypeBilling();
    }

    @Override
    public DocumentType getDocumentTypeDunning() throws OsirisException {
        return getConstants().getDocumentTypeDunning();
    }

    @Override
    public DocumentType getDocumentTypeRefund() throws OsirisException {
        return getConstants().getDocumentTypeRefund();
    }

    @Override
    public TypeDocument getDocumentTypeSynthesisRbeSigned() throws OsirisException {
        return getConstants().getDocumentTypeSynthesisRbeSigned();
    }

    @Override
    public TypeDocument getDocumentTypeSynthesisRbeUnsigned() throws OsirisException {
        return getConstants().getDocumentTypeSynthesisRbeUnsigned();
    }

    @Override
    public TypeDocument getDocumentTypeSynthesisUnsigned() throws OsirisException {
        return getConstants().getDocumentTypeSynthesisUnsigned();
    }

    @Override
    public DocumentType getDocumentTypeBillingClosure() throws OsirisException {
        return getConstants().getDocumentTypeBillingClosure();
    }

    @Override
    public DocumentType getDocumentTypeProvisionnalReceipt() throws OsirisException {
        return getConstants().getDocumentTypeProvisionnalReceipt();
    }

    @Override
    public AttachmentType getAttachmentTypeKbis() throws OsirisException {
        return getConstants().getAttachmentTypeKbis();
    }

    @Override
    public AttachmentType getAttachmentTypeCni() throws OsirisException {
        return getConstants().getAttachmentTypeCni();
    }

    @Override
    public AttachmentType getAttachmentTypeLogo() throws OsirisException {
        return getConstants().getAttachmentTypeLogo();
    }

    @Override
    public AttachmentType getAttachmentTypeProofOfAddress() throws OsirisException {
        return getConstants().getAttachmentTypeProofOfAddress();
    }

    @Override
    public AttachmentType getAttachmentTypeInvoice() throws OsirisException {
        return getConstants().getAttachmentTypeInvoice();
    }

    @Override
    public AttachmentType getAttachmentTypeProviderInvoice() throws OsirisException {
        return getConstants().getAttachmentTypeProviderInvoice();
    }

    @Override
    public AttachmentType getAttachmentTypeCreditNote() throws OsirisException {
        return getConstants().getAttachmentTypeCreditNote();
    }

    @Override
    public AttachmentType getAttachmentTypeKbisUpdated() throws OsirisException {
        return getConstants().getAttachmentTypeKbisUpdated();
    }

    @Override
    public AttachmentType getAttachmentTypeRbe() throws OsirisException {
        return getConstants().getAttachmentTypeRbe();
    }

    @Override
    public AttachmentType getAttachmentTypeDepositReceipt() throws OsirisException {
        return getConstants().getAttachmentTypeDepositReceipt();
    }

    @Override
    public AttachmentType getAttachmentTypePublicationFlag() throws OsirisException {
        return getConstants().getAttachmentTypePublicationFlag();
    }

    @Override
    public AttachmentType getAttachmentTypePublicationReceipt() throws OsirisException {
        return getConstants().getAttachmentTypePublicationReceipt();
    }

    @Override
    public AttachmentType getAttachmentTypePublicationProof() throws OsirisException {
        return getConstants().getAttachmentTypePublicationProof();
    }

    @Override
    public AttachmentType getAttachmentTypeJournal() throws OsirisException {
        return getConstants().getAttachmentTypeJournal();
    }

    @Override
    public AttachmentType getAttachmentTypeBillingClosure() throws OsirisException {
        return getConstants().getAttachmentTypeBillingClosure();
    }

    @Override
    public AttachmentType getAttachmentTypeProofReading() throws OsirisException {
        return getConstants().getAttachmentTypeProofReading();
    }

    @Override
    public AttachmentType getAttachmentTypeAutomaticMail() throws OsirisException {
        return getConstants().getAttachmentTypeAutomaticMail();
    }

    @Override
    public AttachmentType getAttachmentTypeAnnouncement() throws OsirisException {
        return getConstants().getAttachmentTypeAnnouncement();
    }

    @Override
    public AttachmentType getAttachmentTypeComplexAnnouncement() throws OsirisException {
        return getConstants().getAttachmentTypeComplexAnnouncement();
    }

    @Override
    public AttachmentType getAttachmentTypeContract() throws OsirisException {
        return getConstants().getAttachmentTypeContract();
    }

    @Override
    public AttachmentType getAttachmentTypeTemplate() throws OsirisException {
        return getConstants().getAttachmentTypeTemplate();
    }

    @Override
    public AttachmentType getAttachmentTypeQuotation() throws OsirisException {
        return getConstants().getAttachmentTypeQuotation();
    }

    @Override
    public AttachmentType getAttachmentTypeClientCommunication() throws OsirisException {
        return getConstants().getAttachmentTypeClientCommunication();
    }

    @Override
    public AttachmentType getAttachmentTypeApplicationCv() throws OsirisException {
        return getConstants().getAttachmentTypeApplicationCv();
    }

    @Override
    public Country getCountryFrance() throws OsirisException {
        return getConstants().getCountryFrance();
    }

    @Override
    public Country getCountryMonaco() throws OsirisException {
        return getConstants().getCountryMonaco();
    }

    @Override
    public BillingType getBillingTypeDeboursNonTaxable() throws OsirisException {
        return getConstants().getBillingTypeDeboursNonTaxable();
    }

    @Override
    public BillingType getBillingTypeCentralPayFees() throws OsirisException {
        return getConstants().getBillingTypeCentralPayFees();
    }

    @Override
    public BillingType getBillingTypeRff() throws OsirisException {
        return getConstants().getBillingTypeRff();
    }

    @Override
    public BillingType getBillingTypeLogo() throws OsirisException {
        return getConstants().getBillingTypeLogo();
    }

    @Override
    public BillingType getBillingTypeRedactedByJss() throws OsirisException {
        return getConstants().getBillingTypeRedactedByJss();
    }

    @Override
    public BillingType getBillingTypeBaloPackage() throws OsirisException {
        return getConstants().getBillingTypeBaloPackage();
    }

    @Override
    public BillingType getBillingTypeBaloNormalization() throws OsirisException {
        return getConstants().getBillingTypeBaloNormalization();
    }

    @Override
    public BillingType getBillingTypeBaloPublicationFlag() throws OsirisException {
        return getConstants().getBillingTypeBaloPublicationFlag();
    }

    @Override
    public BillingType getBillingTypePublicationPaper() throws OsirisException {
        return getConstants().getBillingTypePublicationPaper();
    }

    @Override
    public BillingType getBillingTypePublicationReceipt() throws OsirisException {
        return getConstants().getBillingTypePublicationReceipt();
    }

    @Override
    public BillingType getBillingTypePublicationFlag() throws OsirisException {
        return getConstants().getBillingTypePublicationFlag();
    }

    @Override
    public BillingType getBillingTypeBodaccFollowup() throws OsirisException {
        return getConstants().getBillingTypeBodaccFollowup();
    }

    @Override
    public BillingType getBillingTypeBodaccFollowupAndRedaction() throws OsirisException {
        return getConstants().getBillingTypeBodaccFollowupAndRedaction();
    }

    @Override
    public BillingType getBillingTypeNantissementDeposit() throws OsirisException {
        return getConstants().getBillingTypeNantissementDeposit();
    }

    @Override
    public BillingType getBillingTypeSocialShareNantissementRedaction() throws OsirisException {
        return getConstants().getBillingTypeSocialShareNantissementRedaction();
    }

    @Override
    public BillingType getBillingTypeBusinnessNantissementRedaction() throws OsirisException {
        return getConstants().getBillingTypeBusinnessNantissementRedaction();
    }

    @Override
    public BillingType getBillingTypeSellerPrivilegeRedaction() throws OsirisException {
        return getConstants().getBillingTypeSellerPrivilegeRedaction();
    }

    @Override
    public BillingType getBillingTypeTreatmentMultipleModiciation() throws OsirisException {
        return getConstants().getBillingTypeTreatmentMultipleModiciation();
    }

    @Override
    public BillingType getBillingTypeVacationMultipleModification() throws OsirisException {
        return getConstants().getBillingTypeVacationMultipleModification();
    }

    @Override
    public BillingType getBillingTypeRegisterPurchase() throws OsirisException {
        return getConstants().getBillingTypeRegisterPurchase();
    }

    @Override
    public BillingType getBillingTypeRegisterInitials() throws OsirisException {
        return getConstants().getBillingTypeRegisterInitials();
    }

    @Override
    public BillingType getBillingTypeRegisterShippingCosts() throws OsirisException {
        return getConstants().getBillingTypeRegisterShippingCosts();
    }

    @Override
    public BillingType getBillingTypeDisbursement() throws OsirisException {
        return getConstants().getBillingTypeDisbursement();
    }

    @Override
    public BillingType getBillingTypeFeasibilityStudy() throws OsirisException {
        return getConstants().getBillingTypeFeasibilityStudy();
    }

    @Override
    public BillingType getBillingTypeChronopostFees() throws OsirisException {
        return getConstants().getBillingTypeChronopostFees();
    }

    @Override
    public BillingType getBillingTypeConfrereFees() throws OsirisException {
        return getConstants().getBillingTypeConfrereFees();
    }

    @Override
    public BillingType getBillingTypeShippingCosts() throws OsirisException {
        return getConstants().getBillingTypeShippingCosts();
    }

    @Override
    public BillingType getBillingTypeApplicationFees() throws OsirisException {
        return getConstants().getBillingTypeApplicationFees();
    }

    @Override
    public BillingType getBillingTypeBankCheque() throws OsirisException {
        return getConstants().getBillingTypeBankCheque();
    }

    @Override
    public BillingType getBillingTypeComplexeFile() throws OsirisException {
        return getConstants().getBillingTypeComplexeFile();
    }

    @Override
    public BillingType getBillingTypeBilan() throws OsirisException {
        return getConstants().getBillingTypeBilan();
    }

    @Override
    public ProvisionType getProvisionTypeBilanPublication() throws OsirisException {
        return getConstants().getProvisionTypeBilanPublication();
    }

    @Override
    public ProvisionType getProvisionTypeRegistrationAct() throws OsirisException {
        return getConstants().getProvisionTypeRegistrationAct();
    }

    @Override
    public ProvisionType getProvisionTypeRbe() throws OsirisException {
        return getConstants().getProvisionTypeRbe();
    }

    @Override
    public ProvisionType getProvisionTypeCharacterAnnouncement() throws OsirisException {
        return getConstants().getProvisionTypeCharacterAnnouncement();
    }

    @Override
    public ProvisionFamilyType getProvisionFamilyTypeDeposit() throws OsirisException {
        return getConstants().getProvisionFamilyTypeDeposit();
    }

    @Override
    public ProvisionFamilyType getProvisionFamilyTypeBodacc() throws OsirisException {
        return getConstants().getProvisionFamilyTypeBodacc();
    }

    @Override
    public ProvisionFamilyType getProvisionFamilyTypeBalo() throws OsirisException {
        return getConstants().getProvisionFamilyTypeBalo();
    }

    @Override
    public ProvisionFamilyType getProvisionFamilyTypeAbonnement() throws OsirisException {
        return getConstants().getProvisionFamilyTypeAbonnement();
    }

    @Override
    public BillingType getBillingTypeInfogreffeDebour() throws OsirisException {
        return getConstants().getBillingTypeInfogreffeDebour();
    }

    @Override
    public BillingType getBillingTypeEmolumentsDeGreffeDebour() throws OsirisException {
        return getConstants().getBillingTypeEmolumentsDeGreffeDebour();
    }

    @Override
    public BillingType getBillingTypeDocumentScanning() throws OsirisException {
        return getConstants().getBillingTypeDocumentScanning();
    }

    @Override
    public BillingType getBillingTypeEmergency() throws OsirisException {
        return getConstants().getBillingTypeEmergency();
    }

    @Override
    public BillingType getBillingTypeRneUpdate() throws OsirisException {
        return getConstants().getBillingTypeRneUpdate();
    }

    @Override
    public BillingType getBillingtypeVacationUpdateBeneficialOwners() throws OsirisException {
        return getConstants().getBillingTypeVacationUpdateBeneficialOwners();
    }

    @Override
    public BillingType getBillingtypeFormalityAdditionalDeclaration() throws OsirisException {
        return getConstants().getBillingTypeFormalityAdditionalDeclaration();
    }

    @Override
    public BillingType getBillingtypeCorrespondenceFees() throws OsirisException {
        return getConstants().getBillingTypeCorrespondenceFees();
    }

    @Override
    public BillingType getBillingTypeDomiciliationContractTypeKeepMail() throws OsirisException {
        return getConstants().getBillingTypeDomiciliationContractTypeKeepMail();
    }

    @Override
    public BillingType getBillingTypeDomiciliationContractTypeRouteEmail() throws OsirisException {
        return getConstants().getBillingTypeDomiciliationContractTypeRouteEmail();
    }

    @Override
    public BillingType getBillingTypeDomiciliationContractTypeRouteMail() throws OsirisException {
        return getConstants().getBillingTypeDomiciliationContractTypeRouteMail();
    }

    @Override
    public BillingType getBillingTypeDomiciliationContractTypeRouteEmailAndMail() throws OsirisException {
        return getConstants().getBillingTypeDomiciliationContractTypeRouteEmailAndMail();
    }

    @Override
    public BillingType getBillingTypeSupplyFullBeCopy() throws OsirisException {
        return getConstants().getBillingTypeSupplyFullBeCopy();
    }

    @Override
    public String getStringNantissementDepositFormeJuridiqueCode() throws OsirisException {
        return getConstants().getStringNantissementDepositFormeJuridiqueCode();
    }

    @Override
    public String getStrinSocialShareNantissementRedactionFormeJuridiqueCode() throws OsirisException {
        return getConstants().getStringNantissementDepositFormeJuridiqueCode();
    }

    @Override
    public String getStringBusinnessNantissementRedactionFormeJuridiqueCode() throws OsirisException {
        return getConstants().getStringNantissementDepositFormeJuridiqueCode();
    }

    @Override
    public PaymentType getPaymentTypeAccount() throws OsirisException {
        return getConstants().getPaymentTypeAccount();
    }

    @Override
    public PaymentType getPaymentTypeEspeces() throws OsirisException {
        return getConstants().getPaymentTypeEspeces();
    }

    @Override
    public PaymentType getPaymentTypeCheques() throws OsirisException {
        return getConstants().getPaymentTypeCheques();
    }

    @Override
    public PaymentType getPaymentTypeCB() throws OsirisException {
        return getConstants().getPaymentTypeCB();
    }

    @Override
    public PaymentType getPaymentTypeVirement() throws OsirisException {
        return getConstants().getPaymentTypeVirement();
    }

    @Override
    public PaymentType getPaymentTypePrelevement() throws OsirisException {
        return getConstants().getPaymentTypePrelevement();
    }

    @Override
    public RefundType getRefundTypeVirement() throws OsirisException {
        return getConstants().getRefundTypeVirement();
    }

    @Override
    public SubscriptionPeriodType getSubscriptionPeriodType12M() throws OsirisException {
        return getConstants().getSubscriptionPeriodType12M();
    }

    @Override
    public LegalForm getLegalFormUnregistered() throws OsirisException {
        return getConstants().getLegalFormUnregistered();
    }

    @Override
    public JournalType getJournalTypeSpel() throws OsirisException {
        return getConstants().getJournalTypeSpel();
    }

    @Override
    public JournalType getJournalTypePaper() throws OsirisException {
        return getConstants().getJournalTypePaper();
    }

    @Override
    public Confrere getConfrereJssSpel() throws OsirisException {
        return getConstants().getConfrereJssSpel();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeKeepMail() throws OsirisException {
        return getConstants().getDomiciliationContractTypeKeepMail();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeRouteMail() throws OsirisException {
        return getConstants().getDomiciliationContractTypeRouteMail();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeRouteEmailAndMail() throws OsirisException {
        return getConstants().getDomiciliationContractTypeRouteEmailAndMail();
    }

    @Override
    public DomiciliationContractType getDomiciliationContractTypeRouteEmail() throws OsirisException {
        return getConstants().getDomiciliationContractTypeRouteEmail();
    }

    @Override
    public MailRedirectionType getMailRedirectionTypeOther() throws OsirisException {
        return getConstants().getMailRedirectionTypeOther();
    }

    @Override
    public MailRedirectionType getMailRedirectionTypeLegalGuardian() throws OsirisException {
        return getConstants().getMailRedirectionTypeLegalGuardian();
    }

    @Override
    public MailRedirectionType getMailRedirectionTypeActivity() throws OsirisException {
        return getConstants().getMailRedirectionTypeActivity();
    }

    @Override
    public ActType getActTypeSeing() throws OsirisException {
        return getConstants().getActTypeSeing();
    }

    @Override
    public ActType getActTypeAuthentic() throws OsirisException {
        return getConstants().getActTypeAuthentic();
    }

    @Override
    public AssignationType getAssignationTypeEmployee() throws OsirisException {
        return getConstants().getAssignationTypeEmployee();
    }

    @Override
    public AssignationType getAssignationTypePublisciste() throws OsirisException {
        return getConstants().getAssignationTypePublisciste();
    }

    @Override
    public AssignationType getAssignationTypeFormaliste() throws OsirisException {
        return getConstants().getAssignationTypeFormaliste();
    }

    @Override
    public Employee getEmployeeBillingResponsible() throws OsirisException {
        return getConstants().getEmployeeBillingResponsible();
    }

    @Override
    public Employee getEmployeeProductionDirector() throws OsirisException {
        return getConstants().getEmployeeProductionDirector();
    }

    @Override
    public Employee getEmployeeMailResponsible() throws OsirisException {
        return getConstants().getEmployeeMailResponsible();
    }

    @Override
    public Employee getEmployeeSalesDirector() throws OsirisException {
        return getConstants().getEmployeeSalesDirector();
    }

    @Override
    public Employee getEmployeeInvoiceReminderResponsible() throws OsirisException {
        return getConstants().getEmployeeInvoiceReminderResponsible();
    }

    @Override
    public Employee getEmployeeCandidacyResponsible() throws OsirisException {
        return getConstants().getEmployeeCandidacyResponsible();
    }

    @Override
    public TransfertFundsType getTransfertFundsTypePhysique() throws OsirisException {
        return getConstants().getTransfertFundsTypePhysique();
    }

    @Override
    public TransfertFundsType getTransfertFundsTypeMoral() throws OsirisException {
        return getConstants().getTransfertFundsTypeMoral();
    }

    @Override
    public TransfertFundsType getTransfertFundsTypeBail() throws OsirisException {
        return getConstants().getTransfertFundsTypeBail();
    }

    @Override
    public CompetentAuthority getCompetentAuthorityInfogreffe() throws OsirisException {
        return getConstants().getCompetentAuthorityInfogreffe();
    }

    @Override
    public CompetentAuthority getCompetentAuthorityInpi() throws OsirisException {
        return getConstants().getCompetentAuthorityInpi();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeRcs() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeRcs();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeCci() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeCci();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeChambreMetier() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeChambreMetier();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeChambreAgriculture() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeChambreAgriculture();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeUrssaf() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeUrssaf();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeDireccte() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeDireccte();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypePrefecture() throws OsirisException {
        return getConstants().getCompetentAuthorityTypePrefecture();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeSpfe() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeSpfe();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeInsee() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeInsee();
    }

    @Override
    public CompetentAuthorityType getCompetentAuthorityTypeCfp() throws OsirisException {
        return getConstants().getCompetentAuthorityTypeCfp();
    }

    @Override
    public InvoiceStatus getInvoiceStatusSend() throws OsirisException {
        return getConstants().getInvoiceStatusSend();
    }

    @Override
    public InvoiceStatus getInvoiceStatusReceived() throws OsirisException {
        return getConstants().getInvoiceStatusReceived();
    }

    @Override
    public InvoiceStatus getInvoiceStatusPayed() throws OsirisException {
        return getConstants().getInvoiceStatusPayed();
    }

    @Override
    public InvoiceStatus getInvoiceStatusCancelled() throws OsirisException {
        return getConstants().getInvoiceStatusCancelled();
    }

    @Override
    public InvoiceStatus getInvoiceStatusCreditNoteEmited() throws OsirisException {
        return getConstants().getInvoiceStatusCreditNoteEmited();
    }

    @Override
    public InvoiceStatus getInvoiceStatusCreditNoteReceived() throws OsirisException {
        return getConstants().getInvoiceStatusCreditNoteReceived();
    }

    @Override
    public Vat getVatTwenty() throws OsirisException {
        return getConstants().getVatTwenty();
    }

    @Override
    public Vat getVatZero() throws OsirisException {
        return getConstants().getVatZero();
    }

    @Override
    public Vat getVatTwo() throws OsirisException {
        return getConstants().getVatTwo();
    }

    @Override
    public Vat getVatDeductible() throws OsirisException {
        return getConstants().getVatDeductible();
    }

    @Override
    public Vat getVatDeductibleTwo() throws OsirisException {
        return getConstants().getVatDeductibleTwo();
    }

    @Override
    public Department getDepartmentMartinique() throws OsirisException {
        return getConstants().getDepartmentMartinique();
    }

    @Override
    public Department getDepartmentGuadeloupe() throws OsirisException {
        return getConstants().getDepartmentGuadeloupe();
    }

    @Override
    public Department getDepartmentReunion() throws OsirisException {
        return getConstants().getDepartmentReunion();
    }

    @Override
    public PublishingDepartment getPublishingDepartmentIdf() throws OsirisException {
        return getConstants().getPublishingDepartmentIdf();
    }

    @Override
    public TypePersonne getTypePersonnePersonnePhysique() throws OsirisException {
        return getConstants().getTypePersonnePersonnePhysique();
    }

    @Override
    public TypePersonne getTypePersonnePersonneMorale() throws OsirisException {
        return getConstants().getTypePersonnePersonneMorale();
    }

    @Override
    public TypePersonne getTypePersonneExploitation() throws OsirisException {
        return getConstants().getTypePersonneExploitation();
    }

    @Override
    public TypeFormalite getTypeFormaliteCessation() throws OsirisException {
        return getConstants().getTypeFormaliteCessation();
    }

    @Override
    public TypeFormalite getTypeFormaliteModification() throws OsirisException {
        return getConstants().getTypeFormaliteModification();
    }

    @Override
    public TypeFormalite getTypeFormaliteCreation() throws OsirisException {
        return getConstants().getTypeFormaliteCreation();
    }

    @Override
    public TypeFormalite getTypeFormaliteCorrection() throws OsirisException {
        return getConstants().getTypeFormaliteCorrection();
    }

    @Override
    public String getStringAccountingSharedMaiblox() throws OsirisException {
        return getConstants().getAccountingSharedMaiblox();
    }

    @Override
    public String getRecoverySharedMaiblox() throws OsirisException {
        return getConstants().getRecoverySharedMaiblox();
    }

    @Override
    public String getStringSalesSharedMailbox() throws OsirisException {
        return getConstants().getSalesSharedMailbox();
    }

    @Override
    public BillingClosureRecipientType getBillingClosureRecipientTypeOther() throws OsirisException {
        return getConstants().getBillingClosureRecipientTypeOther();
    }

    @Override
    public BillingClosureRecipientType getBillingClosureRecipientTypeClient() throws OsirisException {
        return getConstants().getBillingClosureRecipientTypeClient();
    }

    @Override
    public BillingClosureRecipientType getBillingClosureRecipientTypeResponsable() throws OsirisException {
        return getConstants().getBillingClosureRecipientTypeResponsable();
    }

    @Override
    public BillingClosureType getBillingClosureTypeAffaire() throws OsirisException {
        return getConstants().getBillingClosureTypeAffaire();
    }

    @Override
    public DeliveryService getDeliveryServiceJss() throws OsirisException {
        return getConstants().getDeliveryServiceJss();
    }

    @Override
    public Language getLanguageFrench() throws OsirisException {
        return getConstants().getLanguageFrench();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountProvider() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountProvider();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountCustomer() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountCustomer();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountDepositProvider() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountDepositProvider();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountDeposit() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountDeposit();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountLitigious() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountLitigious();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountSuspicious() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountSuspicious();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountProduct() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountProduct();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountCharge() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountCharge();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountBank() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountBank();
    }

    @Override
    public AccountingAccount getAccountingAccountLost() throws OsirisException {
        return getConstants().getAccountingAccountLost();
    }

    @Override
    public AccountingAccount getAccountingAccountProfit() throws OsirisException {
        return getConstants().getAccountingAccountProfit();
    }

    @Override
    public PrincipalAccountingAccount getPrincipalAccountingAccountWaiting() throws OsirisException {
        return getConstants().getPrincipalAccountingAccountWaiting();
    }

    @Override
    public AccountingAccount getAccountingAccountBankCentralPay() throws OsirisException {
        return getConstants().getAccountingAccountBankCentralPay();
    }

    @Override
    public AccountingAccount getAccountingAccountBankJss() throws OsirisException {
        return getConstants().getAccountingAccountBankJss();
    }

    @Override
    public AccountingAccount getAccountingAccountCaisse() throws OsirisException {
        return getConstants().getAccountingAccountCaisse();
    }

    @Override
    public CustomerOrderOrigin getCustomerOrderOriginOldWebSite() throws OsirisException {
        return getConstants().getCustomerOrderOriginWebSite();
    }

    @Override
    public CustomerOrderOrigin getCustomerOrderOriginMyJss() throws OsirisException {
        return getConstants().getCustomerOrderOriginMyJss();
    }

    @Override
    public CustomerOrderOrigin getCustomerOrderOriginOsiris() throws OsirisException {
        return getConstants().getCustomerOrderOriginOsiris();
    }

    @Override
    public PaymentDeadlineType getPaymentDeadLineType30() throws OsirisException {
        return getConstants().getPaymentDeadLineType30();
    }

    @Override
    public Provider getProviderCentralPay() throws OsirisException {
        return getConstants().getProviderCentralPay();
    }

    @Override
    public TiersFollowupType getTiersFollowupTypeInvoiceReminder() throws OsirisException {
        return getConstants().getTiersFollowupTypeInvoiceReminder();
    }

    @Override
    public TiersCategory getTiersCategoryPresse() throws OsirisException {
        return getConstants().getTiersCategoryPresse();
    }

    @Override
    public RffFrequency getRffFrequencyAnnual() throws OsirisException {
        return getConstants().getRffFrequencyAnnual();
    }

    @Override
    public RffFrequency getRffFrequencyMonthly() throws OsirisException {
        return getConstants().getRffFrequencyMonthly();
    }

    @Override
    public RffFrequency getRffFrequencyQuarterly() throws OsirisException {
        return getConstants().getRffFrequencyQuarterly();
    }

    @Override
    public CustomerOrderFrequency getCustomerOrderFrequencyAnnual() throws OsirisException {
        return getConstants().getCustomerOrderFrequencyAnnual();
    }

    @Override
    public CustomerOrderFrequency getCustomerOrderFrequencyMonthly() throws OsirisException {
        return getConstants().getCustomerOrderFrequencyMonthly();
    }

    @Override
    public CustomerOrderFrequency getCustomerOrderFrequencyQuarterly() throws OsirisException {
        return getConstants().getCustomerOrderFrequencyQuarterly();
    }

    @Override
    public ServiceType getServiceTypeOther() throws OsirisException {
        return getConstants().getServiceTypeOther();
    }

    @Override
    public ServiceType getServiceTypeSecondaryCenterOpeningAlAndFormality() throws OsirisException {
        return getConstants().getServiceTypeSecondaryCenterOpeningAlAndFormality();
    }

    @Override
    public ServiceType getServiceTypeAnnualSubscription() throws OsirisException {
        return getConstants().getServiceTypeAnnualSubscription();
    }

    @Override
    public ServiceType getServiceTypeEnterpriseAnnualSubscription() throws OsirisException {
        return getConstants().getServiceTypeEnterpriseAnnualSubscription();
    }

    @Override
    public ServiceType getServiceTypeMonthlySubscription() throws OsirisException {
        return getConstants().getServiceTypeMonthlySubscription();
    }

    @Override
    public ServiceType getServiceTypeKioskNewspaperBuy() throws OsirisException {
        return getConstants().getServiceTypeKioskNewspaperBuy();
    }

    @Override
    public ServiceType getServiceTypeUniqueArticleBuy() throws OsirisException {
        return getConstants().getServiceTypeUniqueArticleBuy();
    }

    @Override
    public SpecialOffer getSpecialOfferJssSubscriptionReduction() throws OsirisException {
        return getConstants().getSpecialOfferJssSubscriptionReduction();
    }

    @Override
    public LocalDate getDateAccountingClosureForAll() throws OsirisException {
        return this.getConstants().getDateAccountingClosureForAll();
    }

    @Override
    public LocalDate getDateAccountingClosureForAccountant() throws OsirisException {
        return this.getConstants().getDateAccountingClosureForAccountant();
    }

    @Override
    public AttachmentType getAttachmentTypeRefusInfogreffe() throws OsirisException {
        return this.getConstants().getAttachmentTypeRefusInfogreffe();
    }

    @Override
    public AttachmentType getAttachmentTypeAutreInfogreffe() throws OsirisException {
        return this.getConstants().getAttachmentTypeAutreInfogreffe();
    }

    @Override
    public ActiveDirectoryGroup getActiveDirectoryGroupFormalites() throws OsirisException {
        return this.getConstants().getActiveDirectoryGroupFormalites();
    }

    @Override
    public ActiveDirectoryGroup getActiveDirectoryGroupDirection() throws OsirisException {
        return this.getConstants().getActiveDirectoryGroupDirection();
    }

    @Override
    public ActiveDirectoryGroup getActiveDirectoryGroupInsertions() throws OsirisException {
        return this.getConstants().getActiveDirectoryGroupInsertions();
    }

    @Override
    public ActiveDirectoryGroup getActiveDirectoryGroupSales() throws OsirisException {
        return this.getConstants().getActiveDirectoryGroupSales();
    }

    @Override
    public ActiveDirectoryGroup getActiveDirectoryGroupFacturation() throws OsirisException {
        return this.getConstants().getActiveDirectoryGroupFacturation();
    }

    @Override
    public ServiceFieldType getFurtherInformationServiceFieldType() throws OsirisException {
        return this.getConstants().getFurtherInformationServiceFieldType();
    }

    @Override
    public Responsable getResponsableDummyCustomerFrance() throws OsirisException {
        return this.getConstants().getResponsableDummyCustomerFrance();
    }

    @Override
    public ProvisionScreenType getProvisionScreenTypeAnnouncement() throws OsirisException {
        return this.getConstants().getProvisionScreenTypeAnnouncement();
    }

    @Override
    public Category getCategoryInterview() throws OsirisException {
        return this.getConstants().getCategoryInterview();
    }

    @Override
    public Category getCategoryPodcast() throws OsirisException {
        return this.getConstants().getCategoryPodcast();
    }

    @Override
    public Category getCategoryArticle() throws OsirisException {
        return this.getConstants().getCategoryArticle();
    }

    @Override
    public Category getCategorySerie() throws OsirisException {
        return this.getConstants().getCategorySerie();
    }

    @Override
    public Category getCategoryExclusivity() throws OsirisException {
        return this.getConstants().getCategoryExclusivity();
    }

    @Override
    public MyJssCategory getMyJssCategoryAnnouncement() throws OsirisException {
        return this.getConstants().getMyJssCategoryAnnouncement();
    }

    @Override
    public MyJssCategory getMyJssCategoryFormality() throws OsirisException {
        return this.getConstants().getMyJssCategoryFormality();
    }

    @Override
    public MyJssCategory getMyJssCategoryDomiciliation() throws OsirisException {
        return this.getConstants().getMyJssCategoryDomiciliation();
    }

    @Override
    public MyJssCategory getMyJssCategoryApostille() throws OsirisException {
        return this.getConstants().getMyJssCategoryApostille();
    }

    @Override
    public MyJssCategory getMyJssCategoryDocument() throws OsirisException {
        return this.getConstants().getMyJssCategoryDocument();
    }

    @Override
    public JssCategory getJssCategoryHomepageFirstHighlighted() throws OsirisException {
        return this.getConstants().getJssCategoryHomepageFirstHighlighted();
    }

    @Override
    public JssCategory getJssCategoryHomepageSecondHighlighted() throws OsirisException {
        return this.getConstants().getJssCategoryHomepageSecondHighlighted();
    }

    @Override
    public JssCategory getJssCategoryHomepageThirdHighlighted() throws OsirisException {
        return this.getConstants().getJssCategoryHomepageThirdHighlighted();
    }

    @Override
    public AccountingAccountClass getAccountingAccountClassProduct() throws OsirisException {
        return this.getConstants().getAccountingAccountClassProduct();
    }

    @Override
    public AccountingAccountClass getAccountingAccountClassTiers() throws OsirisException {
        return this.getConstants().getAccountingAccountClassTiers();
    }

    @Override
    public ServiceFamily getServiceFamilyImmatriculationAlAndFormality() throws OsirisException {
        return this.getConstants().getServiceFamilyImmatriculationAlAndFormality();
    }

    @Override
    public ServiceFamilyGroup getServiceFamilyGroupAnnouncement() throws OsirisException {
        return this.getConstants().getServiceFamilyGroupAnnouncement();
    }

    @Override
    public ServiceFamilyGroup getServiceFamilyGroupFormality() throws OsirisException {
        return this.getConstants().getServiceFamilyGroupFormality();
    }

    @Override
    public ServiceFamilyGroup getServiceFamilyGroupOther() throws OsirisException {
        return this.getConstants().getServiceFamilyGroupOther();
    }

    @Override
    public String getStringMyJssWebinarRequestMail() throws OsirisException {
        return this.getConstants().getStringMyJssWebinarRequestMail();
    }

    @Override
    public String getStringMyJssDemoRequestMail() throws OsirisException {
        return this.getConstants().getStringMyJssDemoRequestMail();
    }

    @Override
    public String getStringMyJssContactFormRequestMail() throws OsirisException {
        return this.getConstants().getStringMyJssContactFormRequestMail();
    }
}
