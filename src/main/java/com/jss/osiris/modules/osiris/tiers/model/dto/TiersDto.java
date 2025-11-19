package com.jss.osiris.modules.osiris.tiers.model.dto;

import java.math.BigDecimal;
import java.util.HashMap;

public class TiersDto {

    private String denomination;
    private Integer id;
    private HashMap<String, BigDecimal> kpiValues;

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HashMap<String, BigDecimal> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(HashMap<String, BigDecimal> kpiValues) {
        this.kpiValues = kpiValues;
    }

}
