package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class KpiCrm implements Serializable, IId {
    // TODO valeur save pour static
    public static String OPPORTUNITY_CLOSING_AVERAGE_TIME = "OPPORTUNITY_CLOSING_AVERAGE_TIME";
    public static String ORDER_COMPLETION_AVERAGE_TIME = "ORDER_COMPLETION_AVERAGE_TIME";
    public static String MEASURED_REVENUE_CUMUL = "MEASURED_REVENUE_CUMUL";
    public static String NB_INVOICE_WITH_LATE_PAYMENT = "NB_INVOICE_WITH_LATE_PAYMENT";
    public static String OVERDUE_BALANCE = "OVERDUE_BALANCE";
    public static String NB_OVERDUE_BALANCE = "NB_OVERDUE_BALANCE";
    public static String PAYING_INVOICE_AVERAGE_TIME = "PAYING_INVOICE_AVERAGE_TIME";
    public static String POTENTIAL_REVENUE_CUMUL = "POTENTIAL_REVENUE_CUMUL";
    public static String DEMO_CUMUL = "DEMO_CUMUL";

    public static final String AGGREGATE_TYPE_AVERAGE = "AGGREGATE_TYPE_AVERAGE";
    public static final String AGGREGATE_TYPE_SUM = "AGGREGATE_TYPE_SUM";

    // ---------- POSSIBLE SCALES OF TIME SELECTABLE FOR KPIS AND WIDGETS ----------
    public static final String WEEKLY_PERIOD = "WEEKLY_PERIOD";
    public static final String MONTHLY_PERIOD = "MONTHLY_PERIOD";
    public static final String ANNUALLY_PERIOD = "ANNUALLY_PERIOD";

    // ---------- PAGES SHOWN IN OSIRIS WITH WIDGETS AND KPIS ------------------
    public static final String TIERS_KPI_HOME_DISPLAY = "TIERS_KPI_HOME_DISPLAY";
    public static final String TIERS_KPI_MAIN_DISPLAY = "TIERS_KPI_MAIN_DISPLAY";
    public static final String TIERS_KPI_BUSINESS_DISPLAY = "TIERS_KPI_BUSINESS_DISPLAY";
    public static final String TIERS_KPI_CUSTOMER_DISPLAY = "TIERS_KPI_CUSTOMER_DISPLAY";
    public static final String CRM_HOME_DISPLAY = "CRM_HOME_DISPLAY";
    public static final List<String> POSSIBLE_DISPLAYS = Arrays.asList(TIERS_KPI_HOME_DISPLAY, TIERS_KPI_MAIN_DISPLAY,
            TIERS_KPI_BUSINESS_DISPLAY, TIERS_KPI_CUSTOMER_DISPLAY, CRM_HOME_DISPLAY);

    @Id
    @SequenceGenerator(name = "kpi_sequence", sequenceName = "kpi_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_sequence")
    private Integer id;

    private String code;

    private String label;

    private LocalDateTime lastUpdate;

    private String unit;

    private Integer displayOrder;

    private String displayedPage;

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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDisplayedPage() {
        return displayedPage;
    }

    public void setDisplayedPage(String displayedPage) {
        this.displayedPage = displayedPage;
    }

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

    public static String getMEASURED_REVENUE_CUMUL() {
        return MEASURED_REVENUE_CUMUL;
    }

    public static void setMEASURED_REVENUE_CUMUL(String mEASURED_REVENUE_CUMUL) {
        MEASURED_REVENUE_CUMUL = mEASURED_REVENUE_CUMUL;
    }

    public static String getNB_INVOICE_WITH_LATE_PAYMENT() {
        return NB_INVOICE_WITH_LATE_PAYMENT;
    }

    public static void setNB_INVOICE_WITH_LATE_PAYMENT(String nB_INVOICE_WITH_LATE_PAYMENT) {
        NB_INVOICE_WITH_LATE_PAYMENT = nB_INVOICE_WITH_LATE_PAYMENT;
    }

    public static String getOVERDUE_BALANCE() {
        return OVERDUE_BALANCE;
    }

    public static void setOVERDUE_BALANCE(String oVERDUE_BALANCE) {
        OVERDUE_BALANCE = oVERDUE_BALANCE;
    }

    public static String getNB_OVERDUE_BALANCE() {
        return NB_OVERDUE_BALANCE;
    }

    public static void setNB_OVERDUE_BALANCE(String nB_OVERDUE_BALANCE) {
        NB_OVERDUE_BALANCE = nB_OVERDUE_BALANCE;
    }

    public static String getPAYING_INVOICE_AVERAGE_TIME() {
        return PAYING_INVOICE_AVERAGE_TIME;
    }

    public static void setPAYING_INVOICE_AVERAGE_TIME(String pAYING_INVOICE_AVERAGE_TIME) {
        PAYING_INVOICE_AVERAGE_TIME = pAYING_INVOICE_AVERAGE_TIME;
    }

    public static String getPOTENTIAL_REVENUE_CUMUL() {
        return POTENTIAL_REVENUE_CUMUL;
    }

    public static void setPOTENTIAL_REVENUE_CUMUL(String pOTENTIAL_REVENUE_CUMUL) {
        POTENTIAL_REVENUE_CUMUL = pOTENTIAL_REVENUE_CUMUL;
    }

    public static String getDEMO_CUMUL() {
        return DEMO_CUMUL;
    }

    public static void setDEMO_CUMUL(String dEMO_CUMUL) {
        DEMO_CUMUL = dEMO_CUMUL;
    }

    public static String getAggregateTypeAverage() {
        return AGGREGATE_TYPE_AVERAGE;
    }

    public static String getAggregateTypeSum() {
        return AGGREGATE_TYPE_SUM;
    }

    public static String getWeeklyPeriod() {
        return WEEKLY_PERIOD;
    }

    public static String getMonthlyPeriod() {
        return MONTHLY_PERIOD;
    }

    public static String getAnnuallyPeriod() {
        return ANNUALLY_PERIOD;
    }

    public static String getTiersKpiHomeDisplay() {
        return TIERS_KPI_HOME_DISPLAY;
    }

    public static String getTiersKpiMainDisplay() {
        return TIERS_KPI_MAIN_DISPLAY;
    }

    public static String getTiersKpiBusinessDisplay() {
        return TIERS_KPI_BUSINESS_DISPLAY;
    }

    public static String getTiersKpiCustomerDisplay() {
        return TIERS_KPI_CUSTOMER_DISPLAY;
    }

    public static String getCrmHomeDisplay() {
        return CRM_HOME_DISPLAY;
    }

    public static List<String> getPossibleDisplays() {
        return POSSIBLE_DISPLAYS;
    }
}
