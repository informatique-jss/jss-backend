package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Voucher implements Serializable, IId {
    @Id
    @SequenceGenerator(name = "voucher_sequence", sequenceName = "voucher_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voucher_sequence")
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private Integer id;

    @Column(nullable = false)
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
    private String code;

    @JsonView({ JacksonViews.OsirisListView.class })
    private LocalDate startDate;
    @JsonView({ JacksonViews.OsirisListView.class })
    private LocalDate endDate;
    @JsonView({ JacksonViews.OsirisListView.class })
    private BigDecimal discountRate;
    @JsonView({ JacksonViews.OsirisListView.class })
    private Integer totalLimit;
    @JsonView({ JacksonViews.OsirisListView.class })
    private Integer perUserLimit;

    @Column(columnDefinition = "NUMERIC(15,2)", precision = 15, scale = 2)
    @JsonView({ JacksonViews.OsirisListView.class })
    private BigDecimal discountAmount;

    @ManyToMany
    @JoinTable(name = "asso_voucher_responsable", joinColumns = @JoinColumn(name = "id_voucher"), inverseJoinColumns = @JoinColumn(name = "id_responseble"))
    @JsonIgnoreProperties(value = { "vouchers" }, allowSetters = true)
    private List<Responsable> responsables;

    @OneToMany(mappedBy = "voucher")
    @JsonIgnoreProperties(value = { "voucher" }, allowSetters = true)
    private List<CustomerOrder> customerOrders;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(Integer totalLimit) {
        this.totalLimit = totalLimit;
    }

    public Integer getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(Integer perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public List<Responsable> getResponsables() {
        return responsables;
    }

    public void setResponsables(List<Responsable> responsables) {
        this.responsables = responsables;
    }

    public List<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<CustomerOrder> customerOrders) {
        this.customerOrders = customerOrders;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

}
