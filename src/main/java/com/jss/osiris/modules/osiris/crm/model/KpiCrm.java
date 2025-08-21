package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class KpiCrm implements Serializable, IId {

    public static String OPPORTUNITY_CLOSING_AVERAGE_TIME = "OPPORTUNITY_CLOSING_AVERAGE_TIME";
    public static String ORDER_COMPLETION_AVERAGE_TIME = "ORDER_COMPLETION_AVERAGE_TIME";
    public static String COMPUTE_TYPE_AVERAGE = "COMPUTE_TYPE_AVERAGE";
    public static String COMPUTE_TYPE_CUMUL = "COMPUTE_TYPE_CUMUL";

    @Id
    @SequenceGenerator(name = "kpi_crm_sequence", sequenceName = "kpi_crm_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_crm_sequence")
    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    private Responsable responsable;

    private LocalDate createdDate;
    private String code;
    private String label;
    private String computeType;
    private BigDecimal value;

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate date) {
        this.createdDate = date;
    }

    public static String getOPPORTUNITY_CLOSING_AVERAGE_TIME() {
        return OPPORTUNITY_CLOSING_AVERAGE_TIME;
    }

    public static void setOPPORTUNITY_CLOSING_AVERAGE_TIME(String oPPORTUNITY_CLOSING_AVERAGE_TIME) {
        OPPORTUNITY_CLOSING_AVERAGE_TIME = oPPORTUNITY_CLOSING_AVERAGE_TIME;
    }

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComputeType() {
        return computeType;
    }

    public void setComputeType(String computeType) {
        this.computeType = computeType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
