package com.jss.osiris.modules.accounting.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Vat;

@Entity
public class AccountingAccount implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	@Column(length = 20)
	private String accountingAccountNumber;

	@ManyToOne
	@JoinColumn(name = "id_vat")
	private Vat vat;

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

	public Vat getVat() {
		return vat;
	}

	public void setVat(Vat vat) {
		this.vat = vat;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
