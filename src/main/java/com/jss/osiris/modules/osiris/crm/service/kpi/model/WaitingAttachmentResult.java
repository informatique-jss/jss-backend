package com.jss.osiris.modules.osiris.crm.service.kpi.model;

import java.math.BigDecimal;
import java.sql.Date;

public class WaitingAttachmentResult {
    private Integer idResponsable;
    private Integer weight;
    private Date resultDate;
    private BigDecimal value;

    public WaitingAttachmentResult(Integer idResponsable, Integer weight, Date resultDate, BigDecimal value) {
        this.idResponsable = idResponsable;
        this.weight = weight;
        this.resultDate = resultDate;
        this.value = value;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Date getResultDate() {
        return resultDate;
    }

    public void setResultDate(Date resultDate) {
        this.resultDate = resultDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
