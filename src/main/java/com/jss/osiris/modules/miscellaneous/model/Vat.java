package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.jss.osiris.modules.accounting.model.AccountingAccount;

@Entity
public class Vat implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	private Float rate;

	private AccountingAccount accountingAccount;

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

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public AccountingAccount getAccountingAccount() {
		return accountingAccount;
	}

	public void setAccountingAccount(AccountingAccount accountingAccount) {
		this.accountingAccount = accountingAccount;
	}

}
