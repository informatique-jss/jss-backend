package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class KpiCrm implements Serializable, IId {

    public static String OPPORTUNITY_CLOSING_AVERAGE_TIME = "OPPORTUNITY_CLOSING_AVERAGE_TIME";
    public static String ORDER_COMPLETION_AVERAGE_TIME = "ORDER_COMPLETION_AVERAGE_TIME";
    public static String COMPUTE_TYPE_AVERAGE = "COMPUTE_TYPE_AVERAGE";
    public static String COMPUTE_TYPE_CUMUL = "COMPUTE_TYPE_CUMUL";

    @Id
    @SequenceGenerator(name = "kpi_sequence", sequenceName = "kpi_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    private Responsable responsable;

    private String code;
    private String label;
    private String computeType;
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "kpi", fetch = FetchType.LAZY)
    private List<KpiCrmValue> kpiValues;

    public static String getOPPORTUNITY_CLOSING_AVERAGE_TIME() {
        return OPPORTUNITY_CLOSING_AVERAGE_TIME;
    }

    public static void setOPPORTUNITY_CLOSING_AVERAGE_TIME(String oPPORTUNITY_CLOSING_AVERAGE_TIME) {
        OPPORTUNITY_CLOSING_AVERAGE_TIME = oPPORTUNITY_CLOSING_AVERAGE_TIME;
    }

    public static String getORDER_COMPLETION_AVERAGE_TIME() {
        return ORDER_COMPLETION_AVERAGE_TIME;
    }

    public static void setORDER_COMPLETION_AVERAGE_TIME(String oRDER_COMPLETION_AVERAGE_TIME) {
        ORDER_COMPLETION_AVERAGE_TIME = oRDER_COMPLETION_AVERAGE_TIME;
    }

    public static String getCOMPUTE_TYPE_AVERAGE() {
        return COMPUTE_TYPE_AVERAGE;
    }

    public static void setCOMPUTE_TYPE_AVERAGE(String cOMPUTE_TYPE_AVERAGE) {
        COMPUTE_TYPE_AVERAGE = cOMPUTE_TYPE_AVERAGE;
    }

    public static String getCOMPUTE_TYPE_CUMUL() {
        return COMPUTE_TYPE_CUMUL;
    }

    public static void setCOMPUTE_TYPE_CUMUL(String cOMPUTE_TYPE_CUMUL) {
        COMPUTE_TYPE_CUMUL = cOMPUTE_TYPE_CUMUL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
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

    public List<KpiCrmValue> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(List<KpiCrmValue> kpiValues) {
        this.kpiValues = kpiValues;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
