package com.jss.osiris.modules.osiris.crm.service.kpi.model;

import java.math.BigDecimal;
import java.sql.Date;

public class WorkingTableTurnoverRatio {
    private Integer idResponsable;
    private Date createdDate;
    private BigDecimal turnover;
    private BigDecimal tiersTurnover;

    public WorkingTableTurnoverRatio(Integer idResponsable, Date createdDate, BigDecimal turnover,
            BigDecimal tiersTurnover) {
        this.idResponsable = idResponsable;
        this.createdDate = createdDate;
        this.turnover = turnover;
        this.tiersTurnover = tiersTurnover;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public BigDecimal getTiersTurnover() {
        return tiersTurnover;
    }

    public void setTiersTurnover(BigDecimal tiersTurnover) {
        this.tiersTurnover = tiersTurnover;
    }

}
