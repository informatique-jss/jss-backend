package com.jss.osiris.modules.osiris.accounting.model;

import java.io.Serializable;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

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
