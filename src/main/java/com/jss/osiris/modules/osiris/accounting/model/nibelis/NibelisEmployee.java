package com.jss.osiris.modules.osiris.accounting.model.nibelis;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class NibelisEmployee {
    private Integer companyId;
    private Integer establishmentId;

    @Id
    private Integer nibelisId;
    private String employeeId;
    private String name;
    private LocalDate hireDate;
    private LocalDate seniorityDate;
    private LocalDate departureDate;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String contractNumber;
    private String presentPeriod;
    private String bulletinPeriod;
    private String multiSheet;
    private Integer lineOrder;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getEstablishmentId() {
        return establishmentId;
    }

    public void setEstablishmentId(Integer establishmentId) {
        this.establishmentId = establishmentId;
    }

    public Integer getNibelisId() {
        return nibelisId;
    }

    public void setNibelisId(Integer nibelisId) {
        this.nibelisId = nibelisId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getSeniorityDate() {
        return seniorityDate;
    }

    public void setSeniorityDate(LocalDate seniorityDate) {
        this.seniorityDate = seniorityDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getPresentPeriod() {
        return presentPeriod;
    }

    public void setPresentPeriod(String presentPeriod) {
        this.presentPeriod = presentPeriod;
    }

    public String getBulletinPeriod() {
        return bulletinPeriod;
    }

    public void setBulletinPeriod(String bulletinPeriod) {
        this.bulletinPeriod = bulletinPeriod;
    }

    public String getMultiSheet() {
        return multiSheet;
    }

    public void setMultiSheet(String multiSheet) {
        this.multiSheet = multiSheet;
    }

    public Integer getLineOrder() {
        return lineOrder;
    }

    public void setLineOrder(Integer order) {
        this.lineOrder = order;
    }

}