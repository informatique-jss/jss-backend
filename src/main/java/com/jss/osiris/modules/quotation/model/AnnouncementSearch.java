package com.jss.osiris.modules.quotation.model;

import java.time.LocalDate;

import com.jss.osiris.modules.miscellaneous.model.Department;

public class AnnouncementSearch {
    private String affaireName;
    private Boolean isStricNameSearch;
    private Department department;
    private LocalDate startDate;
    private LocalDate endDate;
    private NoticeType noticeType;

    public String getAffaireName() {
        return affaireName;
    }

    public void setAffaireName(String affaireName) {
        this.affaireName = affaireName;
    }

    public Boolean getIsStricNameSearch() {
        return isStricNameSearch;
    }

    public void setIsStricNameSearch(Boolean isStricNameSearch) {
        this.isStricNameSearch = isStricNameSearch;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public NoticeType getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(NoticeType noticeType) {
        this.noticeType = noticeType;
    }

}
