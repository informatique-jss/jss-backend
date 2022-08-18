package com.jss.osiris.modules.accounting.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class AccountingRecord implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private Date accountingDate;

	@Column(nullable = false)
	private Date operationDate;
	private String manualAccountingDocumentNumber;
	private String label;
	private Float creditAmount;
	private Float debitAmount;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account")
	private AccountingAccount accountingAccount;

	@Column(nullable = false)
	private Boolean isTemporary;
	// TODO : batch de passage à false chaque jour à minuit

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public String getManualAccountingDocumentNumber() {
		return manualAccountingDocumentNumber;
	}

	public void setManualAccountingDocumentNumber(String manualAccountingDocumentNumber) {
		this.manualAccountingDocumentNumber = manualAccountingDocumentNumber;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Float getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Float creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Float getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Float debitAmount) {
		this.debitAmount = debitAmount;
	}

	public AccountingAccount getAccountingAccount() {
		return accountingAccount;
	}

	public void setAccountingAccount(AccountingAccount accountingAccount) {
		this.accountingAccount = accountingAccount;
	}

}
