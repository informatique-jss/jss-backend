package com.jss.osiris.modules.osiris.crm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

public class AnalyticStatsType {
    @JsonView(JacksonViews.OsirisListView.class)
    private int id;

    private LocalDate valueDate;

    private String icon;

    @JsonView(JacksonViews.OsirisListView.class)
    private AnalyticStatsValue analyticStatsValue;

    @JsonView(JacksonViews.OsirisListView.class)
    private String title;

    @JsonView(JacksonViews.OsirisListView.class)
    private boolean isPositive;

    private BigDecimal percentage;

    private String percentageIcon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public AnalyticStatsValue getAnalyticStatsValue() {
        return analyticStatsValue;
    }

    public void setAnalyticStatsValue(AnalyticStatsValue count) {
        this.analyticStatsValue = count;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getPercentageIcon() {
        return percentageIcon;
    }

    public void setPercentageIcon(String percentageIcon) {
        this.percentageIcon = percentageIcon;
    }

}
