package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;

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
public class Gift implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "gift_sequence", sequenceName = "gift_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gift_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	private Integer stock;

	@Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
	private BigDecimal preTaxPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_accounting_account")
	private AccountingAccount accountingAccount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public AccountingAccount getAccountingAccount() {
		return accountingAccount;
	}

	public void setAccountingAccount(AccountingAccount accountingAccount) {
		this.accountingAccount = accountingAccount;
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

	public BigDecimal getPreTaxPrice() {
		return preTaxPrice;
	}

	public void setPreTaxPrice(BigDecimal preTaxPrice) {
		this.preTaxPrice = preTaxPrice;
	}

}
