package com.jss.osiris.modules.myjss.quotation.controller.model;

import jakarta.persistence.Column;

public class Notice {

    @Column(columnDefinition = "TEXT")
    private String Notice;

    public String getNotice() {
        return Notice;
    }

    public void setNotice(String notice) {
        Notice = notice;
    }

}
