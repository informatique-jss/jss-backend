package com.jss.osiris.modules.osiris.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class KpiWidgetDto {

    private int idKpi;

    private LocalDate valueDate;

    private BigDecimal kpiValue;

    private BigDecimal kpiEvolution;

    private String name;

    private String unit;

    public void setIdKpi(int idKpi) {
        this.idKpi = idKpi;
    }

    public String getName() {
        return name;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public int getIdKpi() {
        return idKpi;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
