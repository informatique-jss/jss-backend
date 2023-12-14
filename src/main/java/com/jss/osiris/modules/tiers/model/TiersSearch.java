package com.jss.osiris.modules.tiers.model;

import java.time.LocalDate;

import com.jss.osiris.modules.profile.model.Employee;

public class TiersSearch {
    private Tiers tiers;
    private Responsable responsable;
    private Employee salesEmployee;
    private LocalDate startDate;
    private LocalDate endDate;
    private String label;

    public Tiers getTiers() {
        return tiers;
    }

    public void setTiers(Tiers tiers) {
        this.tiers = tiers;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

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

}
