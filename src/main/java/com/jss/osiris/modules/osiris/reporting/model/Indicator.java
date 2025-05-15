package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Indicator implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "indicator_sequence", sequenceName = "indicator_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicator_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	@Column(columnDefinition = "TEXT")
	private String sqlText;

	@OneToMany(mappedBy = "indicator", fetch = FetchType.LAZY)
	private List<IndicatorValue> values;

	private LocalDateTime lastUpdate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_indicator_group")
	private IndicatorGroup indicatorGroup;

	@OneToMany(mappedBy = "indicator", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = { "indicator" }, allowSetters = true)
	private List<Kpi> kpis;

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

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public List<IndicatorValue> getValues() {
		return values;
	}

	public void setValues(List<IndicatorValue> values) {
		this.values = values;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public IndicatorGroup getIndicatorGroup() {
		return indicatorGroup;
	}

	public void setIndicatorGroup(IndicatorGroup indicatorGroup) {
		this.indicatorGroup = indicatorGroup;
	}

	public List<Kpi> getKpis() {
		return kpis;
	}

	public void setKpis(List<Kpi> kpis) {
		this.kpis = kpis;
	}

}
