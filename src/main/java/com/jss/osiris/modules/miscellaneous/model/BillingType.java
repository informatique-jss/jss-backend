package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

@Entity
public class BillingType implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
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
	private Boolean isOptionnal;

	@ManyToOne
	@JoinColumn(name = "id_vat")
	private Vat vat;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_product")
	private AccountingAccount accountingAccountProduct;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_charge")
	private AccountingAccount accountingAccountCharge;

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

}
