package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    public static String NB_INVOICE_WITH_LATE_PAYMENT = "NB_INVOICE_WITH_LATE_PAYMENT";
    public static String OVERDUE_BALANCE = "OVERDUE_BALANCE";
    public static String NB_OVERDUE_BALANCE = "NB_OVERDUE_BALANCE";
    public static String PAYING_INVOICE_AVERAGE_TIME = "PAYING_INVOICE_AVERAGE_TIME";
    public static String POTENTIAL_REVENUE_CUMUL = "POTENTIAL_REVENUE_CUMUL";
    public static String DEMO_CUMUL = "DEMO_CUMUL";

    public static final String AGGREGATE_TYPE_AVERAGE = "AGGREGATE_TYPE_AVERAGE";
    public static final String AGGREGATE_TYPE_SUM = "AGGREGATE_TYPE_SUM";

    // ---------- POSSIBLE SCALES OF TIME SELECTABLE FOR KPIS AND WIDGETS ----------
    public static final String WEEKLY_PERIOD = "WEEKLY";
    public static final String MONTHLY_PERIOD = "MONTHLY";
    public static final String ANNUALLY_PERIOD = "ANNUALLY";

    // ---------- PAGES SHOWN IN OSIRIS WITH WIDGETS AND KPIS ------------------
    public static final String TIERS_MAIN_DISPLAY = "TIERS_MAIN_DISPLAY";
    public static final String TIERS_CRM_DISPLAY = "TIERS_CRM_DISPLAY";
    public static final String TIERS_REPORTING_DISPLAY = "TIERS_REPORTING_DISPLAY";
    public static final List<String> POSSIBLE_DISPLAYS = Arrays.asList(TIERS_MAIN_DISPLAY, TIERS_CRM_DISPLAY,
            TIERS_REPORTING_DISPLAY);

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

    private String unit;

    private Integer displayOrder;

    private String displayedPage;

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDisplayedPage() {
        return displayedPage;
    }

    public void setDisplayedPage(String displayedPage) {
        this.displayedPage = displayedPage;
    }
}
