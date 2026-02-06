package com.jss.osiris.modules.osiris.accounting.model.nibelis;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_payslip_month", columnList = "id_nibelis_employee, month"),
})
public class PaySlip {
    @Id
    @SequenceGenerator(name = "pay_slip_sequence", sequenceName = "pay_slip_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_slip_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nibelis_employee")
    private NibelisEmployee employee;

    private String month;
    private Integer lineOrder;
    private Integer sectionId;
    private Integer sectionCode;
    private String sectionType;
    private String sectionTypeLabel;
    private String label;
    private Double number;
    private Double base;
    private Double employeeRate;
    private Double employeeAmount;
    private Double employerRate;
    private Double employerAmount;
    private Double employerCoefficient;

    public Integer getLineOrder() {
        return lineOrder;
    }

    public void setLineOrder(Integer order) {
        this.lineOrder = order;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(Integer sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public String getSectionTypeLabel() {
        return sectionTypeLabel;
    }

    public void setSectionTypeLabel(String sectionTypeLabel) {
        this.sectionTypeLabel = sectionTypeLabel;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getEmployeeRate() {
        return employeeRate;
    }

    public void setEmployeeRate(Double employeeRate) {
        this.employeeRate = employeeRate;
    }

    public Double getEmployeeAmount() {
        return employeeAmount;
    }

    public void setEmployeeAmount(Double employeeAmount) {
        this.employeeAmount = employeeAmount;
    }

    public Double getEmployerRate() {
        return employerRate;
    }

    public void setEmployerRate(Double employerRate) {
        this.employerRate = employerRate;
    }

    public Double getEmployerAmount() {
        return employerAmount;
    }

    public void setEmployerAmount(Double employerAmount) {
        this.employerAmount = employerAmount;
    }

    public Double getEmployerCoefficient() {
        return employerCoefficient;
    }

    public void setEmployerCoefficient(Double employerCoefficient) {
        this.employerCoefficient = employerCoefficient;
    }

    public NibelisEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(NibelisEmployee employee) {
        this.employee = employee;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}