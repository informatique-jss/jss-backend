package com.jss.osiris.modules.quotation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.tiers.model.Tiers;

public class OrderingSearch {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Employee salesEmployee;
    private Employee assignedToEmployee;
    private List<CustomerOrderStatus> customerOrderStatus;
    private List<Tiers> customerOrders;
    private Affaire affaire;
    private Integer idCustomerOrder;
    private Integer idQuotation;
    private Integer idCustomerOrderParentRecurring;
    private Integer idCustomerOrderChildRecurring;
    private Boolean isDisplayOnlyRecurringCustomerOrder;
    private Boolean isDisplayOnlyParentRecurringCustomerOrder;
    private LocalDate recurringValidityDate;

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

    public List<CustomerOrderStatus> getCustomerOrderStatus() {
        return customerOrderStatus;
    }

    public void setCustomerOrderStatus(List<CustomerOrderStatus> customerOrderStatus) {
        this.customerOrderStatus = customerOrderStatus;
    }

    public List<Tiers> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<Tiers> customerOrders) {
        this.customerOrders = customerOrders;
    }

    public Employee getAssignedToEmployee() {
        return assignedToEmployee;
    }

    public void setAssignedToEmployee(Employee assignedToEmployee) {
        this.assignedToEmployee = assignedToEmployee;
    }

    public Integer getIdCustomerOrder() {
        return idCustomerOrder;
    }

    public void setIdCustomerOrder(Integer idCustomerOrder) {
        this.idCustomerOrder = idCustomerOrder;
    }

    public Integer getIdCustomerOrderParentRecurring() {
        return idCustomerOrderParentRecurring;
    }

    public void setIdCustomerOrderParentRecurring(Integer idCustomerOrderParentRecurring) {
        this.idCustomerOrderParentRecurring = idCustomerOrderParentRecurring;
    }

    public Integer getIdCustomerOrderChildRecurring() {
        return idCustomerOrderChildRecurring;
    }

    public void setIdCustomerOrderChildRecurring(Integer idCustomerOrderChildRecurring) {
        this.idCustomerOrderChildRecurring = idCustomerOrderChildRecurring;
    }

    public Boolean getIsDisplayOnlyRecurringCustomerOrder() {
        return isDisplayOnlyRecurringCustomerOrder;
    }

    public void setIsDisplayOnlyRecurringCustomerOrder(Boolean isDisplayOnlyRecurringCustomerOrder) {
        this.isDisplayOnlyRecurringCustomerOrder = isDisplayOnlyRecurringCustomerOrder;
    }

    public Boolean getIsDisplayOnlyParentRecurringCustomerOrder() {
        return isDisplayOnlyParentRecurringCustomerOrder;
    }

    public void setIsDisplayOnlyParentRecurringCustomerOrder(Boolean isDisplayOnlyParentRecurringCustomerOrder) {
        this.isDisplayOnlyParentRecurringCustomerOrder = isDisplayOnlyParentRecurringCustomerOrder;
    }

    public LocalDate getRecurringValidityDate() {
        return recurringValidityDate;
    }

    public void setRecurringValidityDate(LocalDate recurringValidityDate) {
        this.recurringValidityDate = recurringValidityDate;
    }

    public Integer getIdQuotation() {
        return idQuotation;
    }

    public void setIdQuotation(Integer idQuotation) {
        this.idQuotation = idQuotation;
    }

    public Affaire getAffaire() {
        return affaire;
    }

    public void setAffaire(Affaire affaire) {
        this.affaire = affaire;
    }

}
