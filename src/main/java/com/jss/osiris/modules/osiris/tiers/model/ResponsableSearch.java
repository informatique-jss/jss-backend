package com.jss.osiris.modules.osiris.tiers.model;

import java.time.LocalDate;
import java.util.Map;

import com.jss.osiris.modules.osiris.profile.model.Employee;

public class ResponsableSearch {
    private Employee salesEmployee;
    private String mail;
    private String label;

    private Map<String, KpiSearch> kpis;

    private LocalDate startDateKpis;
    private LocalDate endDateKpis;
    private TiersCategory tiersCategory;

    public Employee getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(Employee salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
