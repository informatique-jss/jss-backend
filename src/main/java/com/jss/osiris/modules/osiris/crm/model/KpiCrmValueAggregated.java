package com.jss.osiris.modules.osiris.crm.model;

import java.math.BigDecimal;

public class KpiCrmValueAggregated {
    private BigDecimal value;

    public KpiCrmValueAggregated(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}