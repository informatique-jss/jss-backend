package com.jss.osiris.modules.osiris.accounting.model;

import java.io.Serializable;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class PaySlipLineType implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "pay_slip_line_type_sequence", sequenceName = "pay_slip_line_type_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_slip_line_type_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_debit")
	private AccountingAccount accountingAccountDebit;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_credit")
	private AccountingAccount accountingAccountCredit;

	private String code;

	private Boolean isNotToUse;
	private Boolean isUseEmployerPart;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public AccountingAccount getAccountingAccountDebit() {
		return accountingAccountDebit;
	}

	public void setAccountingAccountDebit(AccountingAccount accountingAccountDebit) {
		this.accountingAccountDebit = accountingAccountDebit;
	}

	public AccountingAccount getAccountingAccountCredit() {
		return accountingAccountCredit;
	}

	public void setAccountingAccountCredit(AccountingAccount accountingAccountCredit) {
		this.accountingAccountCredit = accountingAccountCredit;
	}

	public Boolean getIsNotToUse() {
		return isNotToUse;
	}

	public void setIsNotToUse(Boolean isNotToUse) {
		this.isNotToUse = isNotToUse;
	}

	public Boolean getIsUseEmployerPart() {
		return isUseEmployerPart;
	}

	public void setIsUseEmployerPart(Boolean isUseEmployerPart) {
		this.isUseEmployerPart = isUseEmployerPart;
	}

}
