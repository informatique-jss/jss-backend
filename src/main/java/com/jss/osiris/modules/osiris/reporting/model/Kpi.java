package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.profile.model.Employee;

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
@Table(indexes = { @Index(name = "idx_kpi_indicator", columnList = "id_indicator"),
		@Index(name = "idx_kpi_employee", columnList = "id_employee"),
})
public class Kpi implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "kpi_sequence", sequenceName = "kpi_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_sequence")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDate applicationDate;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal minValue;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal mediumValue;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal maxValue;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal baseValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_indicator")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Indicator indicator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Employee employee;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getMinValue() {
		return minValue;
	}

	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}

	public BigDecimal getMediumValue() {
		return mediumValue;
	}

	public void setMediumValue(BigDecimal mediumValue) {
		this.mediumValue = mediumValue;
	}

	public BigDecimal getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}

	public LocalDate getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(LocalDate applicationDate) {
		this.applicationDate = applicationDate;
	}

	public BigDecimal getBaseValue() {
		return baseValue;
	}

	public void setBaseValue(BigDecimal baseValue) {
		this.baseValue = baseValue;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
