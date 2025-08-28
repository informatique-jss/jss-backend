package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class KpiCrm implements Serializable, IId {
    // TODO valeur save pour static
    public static String OPPORTUNITY_CLOSING_AVERAGE_TIME = "OPPORTUNITY_CLOSING_AVERAGE_TIME";
    public static String ORDER_COMPLETION_AVERAGE_TIME = "ORDER_COMPLETION_AVERAGE_TIME";
    public static String MEASURED_REVENUE_CUMUL = "MEASURED_REVENUE_CUMUL";
    public static String OVERDUE_BALANCE = "OVERDUE_BALANCE";
    public static String PAYING_INVOICE_AVERAGE_TIME = "PAYING_INVOICE_AVERAGE_TIME";
    public static String POTENTIAL_REVENUE_CUMUL = "POTENTIAL_REVENUE_CUMUL";
    public static String AGGREGATE_TYPE_AVERAGE = "AGGREGATE_TYPE_AVERAGE";
    public static String AGGREGATE_TYPE_CUMUL = "AGGREGATE_TYPE_CUMUL";
    public static String AGGREGATE_TYPE_HISTORIC = "AGGREGATE_TYPE_HISTORIC";

    @Id
    @SequenceGenerator(name = "kpi_sequence", sequenceName = "kpi_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_sequence")
    private Integer id;

    private String code;

    private String label;

    private String aggregateType;

    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "kpiCrm", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<KpiCrmValue> kpiValues;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String computeType) {
        this.aggregateType = computeType;
    }

    public List<KpiCrmValue> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(List<KpiCrmValue> kpiValues) {
        this.kpiValues = kpiValues;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
