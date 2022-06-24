package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class BodaccSplit implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@OneToMany(targetEntity = BodaccSplitBeneficiary.class, cascade = CascadeType.ALL)
	private List<BodaccSplitBeneficiary> bodaccSplitBeneficiaries;

	@OneToMany(targetEntity = BodaccSplitCompany.class, cascade = CascadeType.ALL)
	private List<BodaccSplitCompany> bodaccSplitCompanies;

	@Column(nullable = false)
	private Integer assets;

	@Column(nullable = false)
	private Integer liabilities;

	@Column(nullable = false)
	private Integer splitBonus;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String exchangeRatioReport;

	@Column(nullable = false)
	private Date splitProjectDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<BodaccSplitBeneficiary> getBodaccSplitBeneficiaries() {
		return bodaccSplitBeneficiaries;
	}

	public void setBodaccSplitBeneficiaries(List<BodaccSplitBeneficiary> bodaccSplitBeneficiaries) {
		this.bodaccSplitBeneficiaries = bodaccSplitBeneficiaries;
	}

	public List<BodaccSplitCompany> getBodaccSplitCompanies() {
		return bodaccSplitCompanies;
	}

	public void setBodaccSplitCompanies(List<BodaccSplitCompany> bodaccSplitCompanies) {
		this.bodaccSplitCompanies = bodaccSplitCompanies;
	}

	public Integer getAssets() {
		return assets;
	}

	public void setAssets(Integer assets) {
		this.assets = assets;
	}

	public Integer getLiabilities() {
		return liabilities;
	}

	public void setLiabilities(Integer liabilities) {
		this.liabilities = liabilities;
	}

	public Integer getSplitBonus() {
		return splitBonus;
	}

	public void setSplitBonus(Integer splitBonus) {
		this.splitBonus = splitBonus;
	}

	public String getExchangeRatioReport() {
		return exchangeRatioReport;
	}

	public void setExchangeRatioReport(String exchangeRatioReport) {
		this.exchangeRatioReport = exchangeRatioReport;
	}

	public Date getSplitProjectDate() {
		return splitProjectDate;
	}

	public void setSplitProjectDate(Date splitProjectDate) {
		this.splitProjectDate = splitProjectDate;
	}

}
