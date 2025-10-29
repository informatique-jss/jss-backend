package com.jss.osiris.modules.osiris.crm.model;

import java.math.BigDecimal;
import java.util.List;

public interface IKpiCrm {

    public String getCode();

    public String getAggregateType();

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
     * @param responsable
     * @param startDate
     * @param enDate
     * @return
     */
    public List<KpiCrmValue> computeKpiCrmValues();

    public String getLabelType();

}
