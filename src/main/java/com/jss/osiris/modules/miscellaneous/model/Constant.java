package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.accounting.model.AccountingJournal;
import com.jss.osiris.modules.tiers.model.BillingLabelType;
import com.jss.osiris.modules.tiers.model.TiersType;

@Entity
public class Constant implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_code_affaire")
	private BillingLabelType billingLabelTypeCodeAffaire;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_other")
	private BillingLabelType billingLabelTypeOther;

	@ManyToOne
	@JoinColumn(name = "id_billing_label_type_customer")
	private BillingLabelType billingLabelTypeCustomer;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_sales")
	private AccountingJournal accountingJournalSales;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_purchases")
	private AccountingJournal accountingJournalPurchases;

	@ManyToOne
	@JoinColumn(name = "id_accounting_journal_anouveau")
	private AccountingJournal accountingJournalANouveau;

	@ManyToOne
	@JoinColumn(name = "id_tiers_type_prospect")
	private TiersType tiersTypeProspect;

	@ManyToOne
	@JoinColumn(name = "id_document_type_publication")
	private DocumentType documentTypePublication;

	@ManyToOne
	@JoinColumn(name = "id_document_type_cfe")
	private DocumentType documentTypeCfe;

	@ManyToOne
	@JoinColumn(name = "id_document_type_kbis")
	private DocumentType documentTypeKbis;

	@ManyToOne
	@JoinColumn(name = "id_document_type_billing")
	private DocumentType documentTypeBilling;

	@ManyToOne
	@JoinColumn(name = "id_document_type_dunning")
	private DocumentType documentTypeDunning;

	@ManyToOne
	@JoinColumn(name = "id_document_type_refund")
	private DocumentType documentTypeRefund;

	@ManyToOne
	@JoinColumn(name = "id_document_type_billing_closure")
	private DocumentType documentTypeBillingClosure;

	@ManyToOne
	@JoinColumn(name = "id_document_type_provisionnal_receipt")
	private DocumentType documentTypeProvisionnalReceipt;

	@ManyToOne
	@JoinColumn(name = "id_document_type_proof_reading")
	private DocumentType documentTypeProofReading;

	@ManyToOne
	@JoinColumn(name = "id_document_type_publication_certificate")
	private DocumentType documentTypePublicationCertificate;

	@ManyToOne
	@JoinColumn(name = "id_document_type_quotation")
	private DocumentType documentTypeQuotation;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_kbis")
	private DocumentType attachmentTypeKbis;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_cni")
	private DocumentType attachmentTypeCni;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_logo")
	private DocumentType attachmentTypeLogo;

	@ManyToOne
	@JoinColumn(name = "id_attachment_type_proof_of_address")
	private DocumentType attachmentTypeProofOfAddress;

	@ManyToOne
	@JoinColumn(name = "id_country_france")
	private Country countryFrance;

	@ManyToOne
	@JoinColumn(name = "id_country_monaco")
	private Country countryMonaco;

	@ManyToOne
	@JoinColumn(name = "id_billing_type_logo")
	private BillingType billingTypeLogo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BillingLabelType getBillingLabelTypeCodeAffaire() {
		return billingLabelTypeCodeAffaire;
	}

	public void setBillingLabelTypeCodeAffaire(BillingLabelType billingLabelTypeCodeAffaire) {
		this.billingLabelTypeCodeAffaire = billingLabelTypeCodeAffaire;
	}

	public AccountingJournal getAccountingJournalSales() {
		return accountingJournalSales;
	}

	public void setAccountingJournalSales(AccountingJournal accountingJournalSales) {
		this.accountingJournalSales = accountingJournalSales;
	}

	public AccountingJournal getAccountingJournalPurchases() {
		return accountingJournalPurchases;
	}

	public void setAccountingJournalPurchases(AccountingJournal accountingJournalPurchases) {
		this.accountingJournalPurchases = accountingJournalPurchases;
	}

	public AccountingJournal getAccountingJournalANouveau() {
		return accountingJournalANouveau;
	}

	public void setAccountingJournalANouveau(AccountingJournal accountingJournalANouveau) {
		this.accountingJournalANouveau = accountingJournalANouveau;
	}

	public TiersType getTiersTypeProspect() {
		return tiersTypeProspect;
	}

	public void setTiersTypeProspect(TiersType tiersTypeProspect) {
		this.tiersTypeProspect = tiersTypeProspect;
	}

	public DocumentType getDocumentTypePublication() {
		return documentTypePublication;
	}

	public void setDocumentTypePublication(DocumentType documentTypePublication) {
		this.documentTypePublication = documentTypePublication;
	}

	public DocumentType getDocumentTypeCfe() {
		return documentTypeCfe;
	}

	public void setDocumentTypeCfe(DocumentType documentTypeCfe) {
		this.documentTypeCfe = documentTypeCfe;
	}

	public DocumentType getDocumentTypeKbis() {
		return documentTypeKbis;
	}

	public void setDocumentTypeKbis(DocumentType documentTypeKbis) {
		this.documentTypeKbis = documentTypeKbis;
	}

	public DocumentType getDocumentTypeBilling() {
		return documentTypeBilling;
	}

	public void setDocumentTypeBilling(DocumentType documentTypeBilling) {
		this.documentTypeBilling = documentTypeBilling;
	}

	public DocumentType getDocumentTypeDunning() {
		return documentTypeDunning;
	}

	public void setDocumentTypeDunning(DocumentType documentTypeDunning) {
		this.documentTypeDunning = documentTypeDunning;
	}

	public DocumentType getDocumentTypeRefund() {
		return documentTypeRefund;
	}

	public void setDocumentTypeRefund(DocumentType documentTypeRefund) {
		this.documentTypeRefund = documentTypeRefund;
	}

	public DocumentType getDocumentTypeBillingClosure() {
		return documentTypeBillingClosure;
	}

	public void setDocumentTypeBillingClosure(DocumentType documentTypeBillingClosure) {
		this.documentTypeBillingClosure = documentTypeBillingClosure;
	}

	public DocumentType getDocumentTypeProvisionnalReceipt() {
		return documentTypeProvisionnalReceipt;
	}

	public void setDocumentTypeProvisionnalReceipt(DocumentType documentTypeProvisionnlaReceipt) {
		this.documentTypeProvisionnalReceipt = documentTypeProvisionnlaReceipt;
	}

	public DocumentType getDocumentTypeProofReading() {
		return documentTypeProofReading;
	}

	public void setDocumentTypeProofReading(DocumentType documentTypeProofReading) {
		this.documentTypeProofReading = documentTypeProofReading;
	}

	public DocumentType getDocumentTypePublicationCertificate() {
		return documentTypePublicationCertificate;
	}

	public void setDocumentTypePublicationCertificate(DocumentType documentTypePublicationCertificate) {
		this.documentTypePublicationCertificate = documentTypePublicationCertificate;
	}

	public DocumentType getDocumentTypeQuotation() {
		return documentTypeQuotation;
	}

	public void setDocumentTypeQuotation(DocumentType documentTypeQuotation) {
		this.documentTypeQuotation = documentTypeQuotation;
	}

	public DocumentType getAttachmentTypeKbis() {
		return attachmentTypeKbis;
	}

	public void setAttachmentTypeKbis(DocumentType attachmentTypeKbis) {
		this.attachmentTypeKbis = attachmentTypeKbis;
	}

	public DocumentType getAttachmentTypeCni() {
		return attachmentTypeCni;
	}

	public void setAttachmentTypeCni(DocumentType attachmentTypeCni) {
		this.attachmentTypeCni = attachmentTypeCni;
	}

	public DocumentType getAttachmentTypeLogo() {
		return attachmentTypeLogo;
	}

	public void setAttachmentTypeLogo(DocumentType attachmentTypeLogo) {
		this.attachmentTypeLogo = attachmentTypeLogo;
	}

	public DocumentType getAttachmentTypeProofOfAddress() {
		return attachmentTypeProofOfAddress;
	}

	public void setAttachmentTypeProofOfAddress(DocumentType attachmentTypeProofOfAddress) {
		this.attachmentTypeProofOfAddress = attachmentTypeProofOfAddress;
	}

	public BillingLabelType getBillingLabelTypeOther() {
		return billingLabelTypeOther;
	}

	public void setBillingLabelTypeOther(BillingLabelType billingLabelTypeOther) {
		this.billingLabelTypeOther = billingLabelTypeOther;
	}

	public BillingLabelType getBillingLabelTypeCustomer() {
		return billingLabelTypeCustomer;
	}

	public void setBillingLabelTypeCustomer(BillingLabelType billingLabelTypeCustomer) {
		this.billingLabelTypeCustomer = billingLabelTypeCustomer;
	}

	public Country getCountryFrance() {
		return countryFrance;
	}

	public void setCountryFrance(Country countryFrance) {
		this.countryFrance = countryFrance;
	}

	public Country getCountryMonaco() {
		return countryMonaco;
	}

	public void setCountryMonaco(Country countryMonaco) {
		this.countryMonaco = countryMonaco;
	}

	public BillingType getBillingTypeLogo() {
		return billingTypeLogo;
	}

	public void setBillingTypeLogo(BillingType billingTypeLogo) {
		this.billingTypeLogo = billingTypeLogo;
	}

}
