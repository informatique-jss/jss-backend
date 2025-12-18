package com.jss.osiris.modules.osiris.crm.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class KpiCrmCategory {
    public static String QUOTATION = "QUOTATION";
    public static String CUSTOMER_ORDER = "CUSTOMER_ORDER";
    public static String TURNOVER = "TURNOVER";
    public static String ACCOUNTING = "ACCOUNTING";
    public static String WEBSITE = "WEBSITE";

    @Id
    @SequenceGenerator(name = "kpi_crm_category_sequence", sequenceName = "kpi_crm_category_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kpi_crm_category_sequence")
    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private Integer id;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String code;

    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private String label;

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

}
