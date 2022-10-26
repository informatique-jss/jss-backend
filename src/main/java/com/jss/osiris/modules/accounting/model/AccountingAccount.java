package com.jss.osiris.modules.accounting.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = {
		@Index(name = "idx_accounting_account_number", columnList = "accountingAccountNumber,accountingAccountSubNumber", unique = true) })
public class AccountingAccount implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(length = 6, nullable = false)
	private String accountingAccountNumber;

	@Column(length = 20, nullable = false)
	private String accountingAccountSubNumber;

	@ManyToOne
	@JoinColumn(name = "id_accounting_account_class")
	private AccountingAccountClass accountingAccountClass;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public String getAccountingAccountNumber() {
		return accountingAccountNumber;
	}

	public void setAccountingAccountNumber(String accountingAccountNumber) {
		this.accountingAccountNumber = accountingAccountNumber;
	}

	public AccountingAccountClass getAccountingAccountClass() {
		return accountingAccountClass;
	}

	public void setAccountingAccountClass(AccountingAccountClass accountingAccountClass) {
		this.accountingAccountClass = accountingAccountClass;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAccountingAccountSubNumber() {
		return accountingAccountSubNumber;
	}

	public void setAccountingAccountSubNumber(String accountingAccountSubNumber) {
		this.accountingAccountSubNumber = accountingAccountSubNumber;
	}

}
