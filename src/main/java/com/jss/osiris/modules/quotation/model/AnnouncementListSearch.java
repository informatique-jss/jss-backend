package com.jss.osiris.modules.quotation.model;

import java.time.LocalDate;
import java.util.List;

public class AnnouncementListSearch {

    private Confrere confrere;
    private List<AnnouncementStatus> announcementStatus;
    private LocalDate startDate;
    private LocalDate endDate;

    public Confrere getConfrere() {
        return confrere;
    }

    public void setConfrere(Confrere confrere) {
        this.confrere = confrere;
    }

    public List<AnnouncementStatus> getAnnouncementStatus() {
        return announcementStatus;
    }

    public void setAnnouncementStatus(List<AnnouncementStatus> announcementStatus) {
        this.announcementStatus = announcementStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
