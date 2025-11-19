package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class KpiCrm implements Serializable, IId {
    public static final String AGGREGATE_TYPE_AVERAGE = "AGGREGATE_TYPE_AVERAGE";
    public static final String AGGREGATE_TYPE_SUM = "AGGREGATE_TYPE_SUM";

    public static final String GRAPH_TYPE_LINE = "line";
    public static final String GRAPH_TYPE_BAR = "bar";

    // ---------- PAGES SHOWN IN OSIRIS WITH WIDGETS AND KPIS ------------------
    public static final String TIERS_KPI_HOME_DISPLAY = "TIERS_KPI_HOME_DISPLAY";
    public static final String TIERS_KPI_MAIN_DISPLAY = "TIERS_KPI_MAIN_DISPLAY";
    public static final String TIERS_KPI_BUSINESS_DISPLAY = "TIERS_KPI_BUSINESS_DISPLAY";
    public static final String TIERS_KPI_CUSTOMER_DISPLAY = "TIERS_KPI_CUSTOMER_DISPLAY";
    public static final List<String> POSSIBLE_DISPLAYS = Arrays.asList(TIERS_KPI_HOME_DISPLAY, TIERS_KPI_MAIN_DISPLAY,
            TIERS_KPI_BUSINESS_DISPLAY, TIERS_KPI_CUSTOMER_DISPLAY);

    @Id
    @SequenceGenerator(name = "kpi_sequence", sequenceName = "kpi_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_sequence")
    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private Integer id;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String code;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String label;

    @JsonView({ JacksonViews.OsirisListView.class })
    private LocalDateTime lastUpdate;

    @JsonView({ JacksonViews.OsirisListView.class })
    private String unit;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String labelType;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String graphType;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private BigDecimal defaultValue;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private Integer displayOrder;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String displayedPage;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String icon;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private Boolean isPositiveEvolutionGood;

    public static String getTiersKpiHomeDisplay() {
        return TIERS_KPI_HOME_DISPLAY;
    }

    public static String getTiersKpiMainDisplay() {
        return TIERS_KPI_MAIN_DISPLAY;
    }

    public static String getTiersKpiBusinessDisplay() {
        return TIERS_KPI_BUSINESS_DISPLAY;
    }

    public static String getTiersKpiCustomerDisplay() {
        return TIERS_KPI_CUSTOMER_DISPLAY;
    }

    public static List<String> getPossibleDisplays() {
        return POSSIBLE_DISPLAYS;
    }

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

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public static String getAggregateTypeAverage() {
        return AGGREGATE_TYPE_AVERAGE;
    }

    public static String getAggregateTypeSum() {
        return AGGREGATE_TYPE_SUM;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIsPositiveEvolutionGood() {
        return isPositiveEvolutionGood;
    }

    public void setIsPositiveEvolutionGood(Boolean isPositiveEvolutionGood) {
        this.isPositiveEvolutionGood = isPositiveEvolutionGood;
    }

    public BigDecimal getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(BigDecimal defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

}
