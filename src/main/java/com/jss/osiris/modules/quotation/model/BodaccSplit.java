package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class BodaccSplit implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "bodacc_sequence", sequenceName = "bodacc_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bodacc_sequence")
	private Integer id;

	@OneToMany(targetEntity = BodaccSplitBeneficiary.class, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BodaccSplitBeneficiary> bodaccSplitBeneficiaries;

	@OneToMany(targetEntity = BodaccSplitCompany.class, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BodaccSplitCompany> bodaccSplitCompanies;

	private Integer assets;

	private Integer liabilities;

	private Integer splitBonus;

	@Column(columnDefinition = "TEXT")
	private String exchangeRatioReport;

	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate splitProjectDate;

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

	public LocalDate getSplitProjectDate() {
		return splitProjectDate;
	}

	public void setSplitProjectDate(LocalDate splitProjectDate) {
		this.splitProjectDate = splitProjectDate;
	}

}
