package com.jss.osiris.modules.accounting.model;

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

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class PrincipalAccountingAccount implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "principal_accounting_account_sequence", sequenceName = "principal_accounting_account_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "principal_accounting_account_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
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

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public AccountingAccountClass getAccountingAccountClass() {
		return accountingAccountClass;
	}

	public void setAccountingAccountClass(AccountingAccountClass accountingAccountClass) {
		this.accountingAccountClass = accountingAccountClass;
	}

}
