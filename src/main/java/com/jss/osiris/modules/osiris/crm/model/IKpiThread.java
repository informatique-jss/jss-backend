package com.jss.osiris.modules.osiris.crm.model;

import java.math.BigDecimal;

import com.jss.osiris.libs.exception.OsirisException;

public interface IKpiThread {

    public String getCode();

    /**
     * Aggregate type to use when we merge KPIs between Responsable / Tiers
     * 
     * @return
     */
    public String getAggregateTypeForResponsable();

    /**
     * Aggregate type to use when we display one figure for KPIs over a time period
     * 
     * @return
     */
    public String getAggregateTypeForTimePeriod();

    /**
     * Returns the value of the aggregated KpiCrmValue if for a search, the
     * KpiCrmValue is null to allow the calculation of the aggregated value
     * 
     * @return
     */
    public BigDecimal getDefaultValue();

    /**
     * Retreives the values of the kpi that we want to persist in DB
     * 
     * @return
     */
    public void computeKpiCrmValues() throws OsirisException;

    public String getLabelType();

    public String getGraphType();

    public String getKpiCrmCategoryCode();

    public Integer getDisplayOrder();

    public String getUnit();

    public String getIcon();

    /**
     * Set true if a positive evolution from period to period is a good news
     * Set false if it's a bad news
     * 
     * @return
     */
    public Boolean getIsPositiveEvolutionGood();
}
