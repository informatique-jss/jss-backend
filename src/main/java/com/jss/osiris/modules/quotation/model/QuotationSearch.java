package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;

public class QuotationSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Employee salesEmployee;
    private List<QuotationStatus> quotationStatus;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
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

}
