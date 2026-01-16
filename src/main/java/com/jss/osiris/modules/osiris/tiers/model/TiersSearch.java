package com.jss.osiris.modules.osiris.tiers.model;

import java.time.LocalDate;
import java.util.Map;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.modules.osiris.profile.model.Employee;

public class TiersSearch {
    private Employee salesEmployee;
    private String mail;
    private String label;
    private Boolean isNewTiers;

    private Map<String, KpiSearch> kpis;

    private LocalDate startDateKpis;
    private LocalDate endDateKpis;

    // TODO : legacy , to delete post osiris v1
    private LocalDate startDate;
    private LocalDate endDate;
    private IndexEntity tiers;
    private IndexEntity responsable;
    private Boolean withNonNullTurnover;
    private TiersCategory tiersCategory;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Employee getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(Employee salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public IndexEntity getTiers() {
        return tiers;
    }

    public void setTiers(IndexEntity tiers) {
        this.tiers = tiers;
    }

    public IndexEntity getResponsable() {
        return responsable;
    }

    public void setResponsable(IndexEntity responsable) {
        this.responsable = responsable;
    }

    public Boolean getWithNonNullTurnover() {
        return withNonNullTurnover;
    }

    public void setWithNonNullTurnover(Boolean withNonNullTurnover) {
        this.withNonNullTurnover = withNonNullTurnover;
    }

    public Boolean getIsNewTiers() {
        return isNewTiers;
    }

    public void setIsNewTiers(Boolean isNewTiers) {
        this.isNewTiers = isNewTiers;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Map<String, KpiSearch> getKpis() {
        return kpis;
    }

    public void setKpis(Map<String, KpiSearch> kpis) {
        this.kpis = kpis;
    }

    public LocalDate getStartDateKpis() {
        return startDateKpis;
    }

    public void setStartDateKpis(LocalDate startDateKpis) {
        this.startDateKpis = startDateKpis;
    }

    public LocalDate getEndDateKpis() {
        return endDateKpis;
    }

    public void setEndDateKpis(LocalDate endDateKpis) {
        this.endDateKpis = endDateKpis;
    }

    public TiersCategory getTiersCategory() {
        return tiersCategory;
    }

    public void setTiersCategory(TiersCategory tiersCategory) {
        this.tiersCategory = tiersCategory;
    }
}
