package com.jss.osiris.modules.osiris.crm.model;

import java.math.BigDecimal;

public class KpiCrmValueAggregatedByTiers {
    private Integer idTiers;
    private BigDecimal value;

    public KpiCrmValueAggregatedByTiers(Integer idTiers, BigDecimal value) {
        this.idTiers = idTiers;
        this.value = value;
    }

    public Integer getIdTiers() {
        return idTiers;
    }

    public BigDecimal getValue() {
        return value;
    }
}