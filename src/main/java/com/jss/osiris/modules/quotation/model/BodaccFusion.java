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
public class BodaccFusion implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "bodacc_sequence", sequenceName = "bodacc_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bodacc_sequence")
	private Integer id;

	@OneToMany(targetEntity = BodaccFusionMergingCompany.class, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BodaccFusionMergingCompany> bodaccFusionMergingCompanies;

	@OneToMany(targetEntity = BodaccFusionAbsorbedCompany.class, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BodaccFusionAbsorbedCompany> bodaccFusionAbsorbedCompanies;

	private Integer assets;

	private Integer liabilities;

	private Integer mergerBonus;

	@Column(columnDefinition = "TEXT")
	private String exchangeRatioReport;

	@Column(nullable = false)
	@JsonSerialize(using = JacksonLocalDateSerializer.class)
	private LocalDate mergingProjectDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<BodaccFusionMergingCompany> getBodaccFusionMergingCompanies() {
		return bodaccFusionMergingCompanies;
	}

	public void setBodaccFusionMergingCompanies(List<BodaccFusionMergingCompany> bodaccFusionMergingCompanies) {
		this.bodaccFusionMergingCompanies = bodaccFusionMergingCompanies;
	}

	public List<BodaccFusionAbsorbedCompany> getBodaccFusionAbsorbedCompanies() {
		return bodaccFusionAbsorbedCompanies;
	}

	public void setBodaccFusionAbsorbedCompanies(List<BodaccFusionAbsorbedCompany> bodaccFusionAbsorbedCompanies) {
		this.bodaccFusionAbsorbedCompanies = bodaccFusionAbsorbedCompanies;
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

	public Integer getMergerBonus() {
		return mergerBonus;
	}

	public void setMergerBonus(Integer mergerBonus) {
		this.mergerBonus = mergerBonus;
	}

	public String getExchangeRatioReport() {
		return exchangeRatioReport;
	}

	public void setExchangeRatioReport(String exchangeRatioReport) {
		this.exchangeRatioReport = exchangeRatioReport;
	}

	public LocalDate getMergingProjectDate() {
		return mergingProjectDate;
	}

	public void setMergingProjectDate(LocalDate mergingProjectDate) {
		this.mergingProjectDate = mergingProjectDate;
	}

}
