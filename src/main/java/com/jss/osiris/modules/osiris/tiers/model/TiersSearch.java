package com.jss.osiris.modules.osiris.tiers.model;

import java.time.LocalDate;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.profile.model.Employee;

public class TiersSearch {
    private IndexEntity tiers;
    private IndexEntity responsable;
    private Boolean withNonNullTurnover;
    private Employee salesEmployee;
    private Mail mail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String label;
    private Boolean isNewTiers;

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

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

}
