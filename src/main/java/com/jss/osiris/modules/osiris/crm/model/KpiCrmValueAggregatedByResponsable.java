package com.jss.osiris.modules.osiris.crm.model;

import java.math.BigDecimal;

public class KpiCrmValueAggregatedByResponsable {
    private Integer idResponsable;
    private BigDecimal value;

    public KpiCrmValueAggregatedByResponsable(Integer idResponsable, BigDecimal value) {
        this.idResponsable = idResponsable;
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }
}