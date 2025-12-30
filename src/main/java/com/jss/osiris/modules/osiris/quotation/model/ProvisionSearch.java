package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDate;

import com.jss.osiris.modules.osiris.profile.model.Employee;

public class ProvisionSearch {

    private Employee salesEmployee;
    private Employee FormalisteEmployee;
    private String mail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer responsable;
    private SimpleProvisionStatus provisionStatus;
    private String affaire;
    private String waitingCompetentAuthoritySearch;
    private String guichetUniqueStatus;
    private String infogreffeStatus;

    public Employee getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(Employee salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public Employee getFormalisteEmployee() {
        return FormalisteEmployee;
    }

    public void setFormalisteEmployee(Employee formalisteEmployee) {
        this.FormalisteEmployee = formalisteEmployee;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    public Integer getResponsable() {
        return responsable;
    }

    public void setResponsables(Integer responsable) {
        this.responsable = responsable;
    }

    public SimpleProvisionStatus getProvisionStatus() {
        return provisionStatus;
    }

    public void setProvisionStatus(SimpleProvisionStatus provisionStatus) {
        this.provisionStatus = provisionStatus;
    }

    public String getAffaire() {
        return affaire;
    }

    public void setAffaire(String affaire) {
        this.affaire = affaire;
    }

    public String getWaitingCompetentAuthoritySearch() {
        return waitingCompetentAuthoritySearch;
    }

    public void setWaitingCompetentAuthoritySearch(String waitingCompetentAuthoritySearch) {
        this.waitingCompetentAuthoritySearch = waitingCompetentAuthoritySearch;
    }

    public String getGuichetUniqueStatus() {
        return guichetUniqueStatus;
    }

    public void setGuichetUniqueStatus(String guichetUniqueStatus) {
        this.guichetUniqueStatus = guichetUniqueStatus;
    }

    public String getInfogreffeStatus() {
        return infogreffeStatus;
    }

    public void setInfogreffeStatus(String infogreffeStatus) {
        this.infogreffeStatus = infogreffeStatus;
    }

}
