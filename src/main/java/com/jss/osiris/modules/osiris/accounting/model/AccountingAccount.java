package com.jss.osiris.modules.osiris.accounting.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
		@Index(name = "idx_accounting_account_number", columnList = "id_principal_accounting_account,accountingAccountSubNumber", unique = true),
		@Index(name = "idx_accounting_account_principal", columnList = "id_principal_accounting_account") })
public class AccountingAccount implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "accounting_account_sequence", sequenceName = "accounting_account_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounting_account_sequence")
	@JsonView({ JacksonViews.OsirisListView.class })
	private Integer id;

	@Column(nullable = false, length = 200)
	private String label;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_principal_accounting_account")
	@JsonView({ JacksonViews.OsirisListView.class })
	private PrincipalAccountingAccount principalAccountingAccount;

	@JsonView({ JacksonViews.OsirisListView.class })
	private Integer accountingAccountSubNumber;

	@Column(nullable = false)
	private Boolean isViewRestricted;

	private Boolean isAllowedToPutIntoAccount;

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

	public Boolean getIsAllowedToPutIntoAccount() {
		return isAllowedToPutIntoAccount;
	}

	public void setIsAllowedToPutIntoAccount(Boolean isAllowedToPutIntoAccount) {
		this.isAllowedToPutIntoAccount = isAllowedToPutIntoAccount;
	}
}
