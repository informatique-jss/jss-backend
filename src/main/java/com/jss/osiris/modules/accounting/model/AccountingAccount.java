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
		@Index(name = "idx_accounting_account_number", columnList = "id_principal_accounting_account,accountingAccountSubNumber", unique = true) })
public class AccountingAccount implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@ManyToOne
	@JoinColumn(name = "id_principal_accounting_account")
	private PrincipalAccountingAccount principalAccountingAccount;

	private Integer accountingAccountSubNumber;

	@Column(nullable = false)
	private Boolean isViewRestricted;

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

	public Integer getAccountingAccountSubNumber() {
		return accountingAccountSubNumber;
	}

	public void setAccountingAccountSubNumber(Integer accountingAccountSubNumber) {
		this.accountingAccountSubNumber = accountingAccountSubNumber;
	}

	public PrincipalAccountingAccount getPrincipalAccountingAccount() {
		return principalAccountingAccount;
	}

	public void setPrincipalAccountingAccount(PrincipalAccountingAccount principalAccountingAccount) {
		this.principalAccountingAccount = principalAccountingAccount;
	}

	public Boolean getIsViewRestricted() {
		return isViewRestricted;
	}

	public void setIsViewRestricted(Boolean isViewRestricted) {
		this.isViewRestricted = isViewRestricted;
	}

}
