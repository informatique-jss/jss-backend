package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@DoNotAudit
@Entity
public class KpiCrmValue implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "kpi_value_sequence", sequenceName = "kpi_value_sequence", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_value_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kpi")
    @JsonIgnoreProperties(value = { "kpiValues" }, allowSetters = true)
    private KpiCrm kpiCrm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    private Responsable responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sales_employee_overrided")
    private Employee overridedSalesEmployee;

    private LocalDate valueDate;

    private BigDecimal value;
    private Integer weight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KpiCrm getKpiCrm() {
        return kpiCrm;
    }

    public void setKpiCrm(KpiCrm kpiCrm) {
        this.kpiCrm = kpiCrm;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate dateValue) {
        this.valueDate = dateValue;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Employee getOverridedSalesEmployee() {
        return overridedSalesEmployee;
    }

    public void setOverridedSalesEmployee(Employee overridedSalesEmployee) {
        this.overridedSalesEmployee = overridedSalesEmployee;
    }

}
