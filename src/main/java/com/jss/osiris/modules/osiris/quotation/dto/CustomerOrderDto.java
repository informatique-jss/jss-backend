package com.jss.osiris.modules.osiris.quotation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerOrderDto {

    private Integer id;
    private String salesEmployee;
    private LocalDateTime creationDate;
    private String status;
    private String responsable;
    private List<String> affaires;
    private List<String> services;
    private String origin;
    private String description;
    private String tiers;
    private BigDecimal totalPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(String salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime startDate) {
        this.creationDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponsablesIds() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public List<String> getAffaires() {
        return affaires;
    }

    public void setAffaires(List<String> affaire) {
        this.affaires = affaire;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> service) {
        this.services = service;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTiers() {
        return tiers;
    }

    public void setTiers(String tiers) {
        this.tiers = tiers;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}