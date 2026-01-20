package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public class QuotationSearch {
    private LocalDate startDate;
    private LocalDateTime endDate;
    private Employee salesEmployee;
    private List<QuotationStatus> quotationStatus;
    private List<Tiers> customerOrders;
    private List<Affaire> affaires;
    private List<Responsable> responsables;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Employee getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(Employee salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public List<QuotationStatus> getQuotationStatus() {
        return quotationStatus;
    }

    public void setQuotationStatus(List<QuotationStatus> quotationStatus) {
        this.quotationStatus = quotationStatus;
    }

    public List<Tiers> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<Tiers> customerOrders) {
        this.customerOrders = customerOrders;
    }

    public List<Affaire> getAffaires() {
        return affaires;
    }

    public void setAffaires(List<Affaire> affaires) {
        this.affaires = affaires;
    }

    public List<Responsable> getResponsables() {
        return responsables;
    }

    public void setResponsables(List<Responsable> responsables) {
        this.responsables = responsables;
    }

}
