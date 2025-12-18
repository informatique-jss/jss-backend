package com.jss.osiris.modules.osiris.crm.service.kpi.model;

import java.math.BigDecimal;
import java.sql.Date;

public class WorkingTableTurnover {
    private Integer idResponsable;
    private Date createdDate;
    private BigDecimal turnover;

    public WorkingTableTurnover(Integer idResponsable, Date createdDate, BigDecimal turnover) {
        this.idResponsable = idResponsable;
        this.createdDate = createdDate;
        this.turnover = turnover;
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

}
