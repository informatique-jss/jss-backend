package com.jss.osiris.modules.osiris.crm.dto;

import java.math.BigDecimal;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;

public class KpiWidgetDto {

    private BigDecimal kpiValue;

    private BigDecimal kpiEvolution;

    private KpiCrm kpiCrm;

    private String labelType;

    public BigDecimal getKpiValue() {
        return kpiValue;
    }

    public void setKpiValue(BigDecimal kpiValue) {
        this.kpiValue = kpiValue;
    }

    public BigDecimal getKpiEvolution() {
        return kpiEvolution;
    }

    public void setKpiEvolution(BigDecimal kpiEvolution) {
        this.kpiEvolution = kpiEvolution;
    }

    public KpiCrm getKpiCrm() {
        return kpiCrm;
    }

    public void setKpiCrm(KpiCrm kpiCrm) {
        this.kpiCrm = kpiCrm;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

}
