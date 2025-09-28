package com.jss.osiris.modules.osiris.reporting.model;

import java.util.Arrays;
import java.util.List;

public class ReportingUpdateFrequency {
    public static String HOURLY = "HOURLY";
    public static String DAILY = "DAILY";

    private String code;
    private String label;

    public static List<ReportingUpdateFrequency> getAllReporingFrequency() {
        return Arrays.asList(new ReportingUpdateFrequency(HOURLY, "Horaire"),
                new ReportingUpdateFrequency(DAILY, "Quotidien"));
    }

    public ReportingUpdateFrequency(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static String getHOURLY() {
        return HOURLY;
    }

    public static void setHOURLY(String hOURLY) {
        HOURLY = hOURLY;
    }

    public static String getDAILY() {
        return DAILY;
    }

    public static void setDAILY(String dAILY) {
        DAILY = dAILY;
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
