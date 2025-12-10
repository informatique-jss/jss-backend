package com.jss.osiris.modules.osiris.crm.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KpiCrmSearchModel {
    LocalDate endDateKpis;
    LocalDate startDateKpis;
    Integer salesEmployeeId;
    List<Integer> tiersIds;
    List<Integer> responsableIds;

    @JsonProperty("isAllTiers")
    boolean isAllTiers;
    String kpiScale;

    public LocalDate getEndDateKpis() {
        return endDateKpis;
    }

    public void setEndDateKpis(LocalDate endDateKpis) {
        this.endDateKpis = endDateKpis;
    }

    public LocalDate getStartDateKpis() {
        return startDateKpis;
    }

    public void setStartDateKpis(LocalDate startDateKpis) {
        this.startDateKpis = startDateKpis;
    }

    public Integer getSalesEmployeeId() {
        return salesEmployeeId;
    }

    public void setSalesEmployeeId(Integer salesEmployeeId) {
        this.salesEmployeeId = salesEmployeeId;
    }

    public List<Integer> getTiersIds() {
        return tiersIds;
    }

    public void setTiersIds(List<Integer> tiersIds) {
        this.tiersIds = tiersIds;
    }

    public List<Integer> getResponsableIds() {
        return responsableIds;
    }

    public void setResponsableIds(List<Integer> responsableIds) {
        this.responsableIds = responsableIds;
    }

    public boolean isAllTiers() {
        return isAllTiers;
    }

    public void setAllTiers(boolean isAllTiers) {
        this.isAllTiers = isAllTiers;
    }

    public String getKpiScale() {
        return kpiScale;
    }

    public void setKpiScale(String kpiScale) {
        this.kpiScale = kpiScale;
    }

}
