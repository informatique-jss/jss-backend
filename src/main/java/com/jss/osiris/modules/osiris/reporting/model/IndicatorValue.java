package com.jss.osiris.modules.osiris.reporting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(indexes = { @Index(name = "idx_indicator_value_indicator", columnList = "id_indicator"),
		@Index(name = "idx_indicator_value_indicator_employee", columnList = "id_indicator, id_employee"),
		@Index(name = "idx_indicator_value_employee", columnList = "id_employee"),
})
public class IndicatorValue implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "indicator_value_sequence", sequenceName = "indicator_value_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicator_value_sequence")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_indicator")
	@JsonView({ JacksonViews.OsirisDetailedView.class })
	@JsonIgnoreProperties(value = { "values" }, allowSetters = true)
	private Indicator indicator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Employee employee;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private LocalDate date;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal value;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Boolean isMinValueReached;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Boolean isMediumValueReached;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Boolean isMaxValueReached;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private BigDecimal succededValue;

	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
	private Float succededPercentage;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Boolean getIsMinValueReached() {
		return isMinValueReached;
	}

	public void setIsMinValueReached(Boolean isMinValueReached) {
		this.isMinValueReached = isMinValueReached;
	}

	public Boolean getIsMediumValueReached() {
		return isMediumValueReached;
	}

	public void setIsMediumValueReached(Boolean isMediumValueReached) {
		this.isMediumValueReached = isMediumValueReached;
	}

	public Boolean getIsMaxValueReached() {
		return isMaxValueReached;
	}

	public void setIsMaxValueReached(Boolean isMaxValueReached) {
		this.isMaxValueReached = isMaxValueReached;
	}

	public BigDecimal getSuccededValue() {
		return succededValue;
	}

	public void setSuccededValue(BigDecimal succededValue) {
		this.succededValue = succededValue;
	}

	public Float getSuccededPercentage() {
		return succededPercentage;
	}

	public void setSuccededPercentage(Float succededPercentage) {
		this.succededPercentage = succededPercentage;
	}

}
