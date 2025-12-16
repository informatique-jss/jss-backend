package com.jss.osiris.modules.osiris.tiers.model.dto;

import java.util.HashMap;
import java.util.List;

public class ResponsableDto {

    private String civility;
    private String firstname;
    private String lastname;
    private Integer id;
    private HashMap<String, String> kpiValues;
    private Integer tiersId;
    private String tiersDenomination;
    private String tiersCategory;
    private String responsableCategory;
    private String salesEmployee;
    private String formalisteEmployee;

    // Detailed informations
    private Boolean isActive;
    private String mail;
    private List<String> phones;
    private String function;
    private String mailRecipient;
    private Integer numberOfGiftPostsPerMonth;
    private Boolean canViewAllTiersInWeb;
    private String observations;

    // Getters Setters
    public String getCivility() {
        return civility;
    }

    public void setCivility(String civility) {
        this.civility = civility;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HashMap<String, String> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(HashMap<String, String> kpiValues) {
        this.kpiValues = kpiValues;
    }

    public Integer getTiersId() {
        return tiersId;
    }

    public void setTiersId(Integer tiersId) {
        this.tiersId = tiersId;
    }

    public String getTiersDenomination() {
        return tiersDenomination;
    }

    public void setTiersDenomination(String tiersDenomination) {
        this.tiersDenomination = tiersDenomination;
    }

    public String getTiersCategory() {
        return tiersCategory;
    }

    public void setTiersCategory(String tiersCategory) {
        this.tiersCategory = tiersCategory;
    }

    public String getResponsableCategory() {
        return responsableCategory;
    }

    public void setResponsableCategory(String responsableCategory) {
        this.responsableCategory = responsableCategory;
    }

    public String getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(String salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getFormalisteEmployee() {
        return formalisteEmployee;
    }

    public void setFormalisteEmployee(String formalisteEmployee) {
        this.formalisteEmployee = formalisteEmployee;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getMailRecipient() {
        return mailRecipient;
    }

    public void setMailRecipient(String mailRecipient) {
        this.mailRecipient = mailRecipient;
    }

    public Integer getNumberOfGiftPostsPerMonth() {
        return numberOfGiftPostsPerMonth;
    }

    public void setNumberOfGiftPostsPerMonth(Integer numberOfGiftPostsPerMonth) {
        this.numberOfGiftPostsPerMonth = numberOfGiftPostsPerMonth;
    }

    public Boolean getCanViewAllTiersInWeb() {
        return canViewAllTiersInWeb;
    }

    public void setCanViewAllTiersInWeb(Boolean canViewAllTiersInWeb) {
        this.canViewAllTiersInWeb = canViewAllTiersInWeb;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
