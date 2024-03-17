package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

@Entity
public class BillingType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "billing_type_sequence", sequenceName = "billing_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billing_type_sequence")
	private Integer id;

	@Column(nullable = false, length = 255)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@Column(nullable = false)
	private Boolean canOverridePrice;

	@Column(nullable = false)
	private Boolean isPriceBasedOnCharacterNumber;

	@Column(nullable = false)
	private Boolean isOverrideVat;

	@Column(nullable = false)
	private Boolean isNonTaxable;

	@Column(nullable = false)
	private Boolean isDebour;

	private Boolean isFee;

	@Column(nullable = false)
	private Boolean isOptionnal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vat")
	private Vat vat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_product")
	private AccountingAccount accountingAccountProduct;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account_charge")
	private AccountingAccount accountingAccountCharge;

	@Column(nullable = false)
	private Boolean isGenerateAccountProduct;

	@Column(nullable = false)
	private Boolean isGenerateAccountCharge;

	private Boolean isUsedForInsertionRff;
	private Boolean isUsedForFormaliteRff;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getCanOverridePrice() {
		return canOverridePrice;
	}

	public void setCanOverridePrice(Boolean canOverridePrice) {
		this.canOverridePrice = canOverridePrice;
	}

	public Boolean getIsPriceBasedOnCharacterNumber() {
		return isPriceBasedOnCharacterNumber;
	}

	public void setIsPriceBasedOnCharacterNumber(Boolean isPriceBasedOnCharacterNumber) {
		this.isPriceBasedOnCharacterNumber = isPriceBasedOnCharacterNumber;
	}

	public Boolean getIsOverrideVat() {
		return isOverrideVat;
	}

	public void setIsOverrideVat(Boolean isOverrideVat) {
		this.isOverrideVat = isOverrideVat;
	}

	public Vat getVat() {
		return vat;
	}

	public void setVat(Vat vat) {
		this.vat = vat;
	}

	public Boolean getIsOptionnal() {
		return isOptionnal;
	}

	public void setIsOptionnal(Boolean isOptionnal) {
		this.isOptionnal = isOptionnal;
	}

	public AccountingAccount getAccountingAccountProduct() {
		return accountingAccountProduct;
	}

	public void setAccountingAccountProduct(AccountingAccount accountingAccountProduct) {
		this.accountingAccountProduct = accountingAccountProduct;
	}

	public AccountingAccount getAccountingAccountCharge() {
		return accountingAccountCharge;
	}

	public void setAccountingAccountCharge(AccountingAccount accountingAccountCharge) {
		this.accountingAccountCharge = accountingAccountCharge;
	}

	public Boolean getIsGenerateAccountProduct() {
		return isGenerateAccountProduct;
	}

	public void setIsGenerateAccountProduct(Boolean isGenerateAccountProduct) {
		this.isGenerateAccountProduct = isGenerateAccountProduct;
	}

	public Boolean getIsGenerateAccountCharge() {
		return isGenerateAccountCharge;
	}

	public void setIsGenerateAccountCharge(Boolean isGenerateAccountCharge) {
		this.isGenerateAccountCharge = isGenerateAccountCharge;
	}

	public Boolean getIsNonTaxable() {
		return isNonTaxable;
	}

	public void setIsNonTaxable(Boolean isNonTaxable) {
		this.isNonTaxable = isNonTaxable;
	}

	public Boolean getIsDebour() {
		return isDebour;
	}

	public void setIsDebour(Boolean isDebour) {
		this.isDebour = isDebour;
	}

	public Boolean getIsFee() {
		return isFee;
	}

	public void setIsFee(Boolean isFee) {
		this.isFee = isFee;
	}

	public Boolean getIsUsedForInsertionRff() {
		return isUsedForInsertionRff;
	}

	public void setIsUsedForInsertionRff(Boolean isUsedForInsertionRff) {
		this.isUsedForInsertionRff = isUsedForInsertionRff;
	}

	public Boolean getIsUsedForFormaliteRff() {
		return isUsedForFormaliteRff;
	}

	public void setIsUsedForFormaliteRff(Boolean isUsedForFormaliteRff) {
		this.isUsedForFormaliteRff = isUsedForFormaliteRff;
	}

}
