package com.jss.osiris.modules.osiris.tiers.model.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;

public class TiersDto {

    private String denomination;
    private Integer id;
    private HashMap<String, String> kpiValues;
    private String salesEmployee;
    private String formalisteEmployee;
    private Boolean isNewTiers;
    private String address;
    private String postalCode;
    private String cedexComplement;
    private String siret;
    private City city;
    private Country country;

    // Details
    private String tiersCategory;
    private String tiersType;
    private List<String> phones;
    private List<String> mails;
    private String mailRecipient;
    private BigDecimal rffFormaliteRate;
    private BigDecimal rffInsertionRate;
    private List<String> specialOffers;
    private String instructions;
    private String observations;
    private List<String> competitors;
    private String accountingAccountCustomer;
    private String accountingAccountDeposit;

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSalesEmployee() {
        return salesEmployee;
    }

    public void setSalesEmployee(String salesEmployee) {
        this.salesEmployee = salesEmployee;
    }

    public String getFormalisteEmployee() {
        return formalisteEmployee;
    }

    public void setFormalisteEmployee(String formalisteEmployee) {
        this.formalisteEmployee = formalisteEmployee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCedexComplement() {
        return cedexComplement;
    }

    public void setCedexComplement(String cedexComplement) {
        this.cedexComplement = cedexComplement;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getTiersCategory() {
        return tiersCategory;
    }

    public void setTiersCategory(String tiersCategory) {
        this.tiersCategory = tiersCategory;
    }

    public boolean getIsNewTiers() {
        return isNewTiers;
    }

    public void setIsNewTiers(Boolean isNewTiers) {
        this.isNewTiers = isNewTiers;
    }

    public HashMap<String, String> getKpiValues() {
        return kpiValues;
    }

    public void setKpiValues(HashMap<String, String> kpiValues) {
        this.kpiValues = kpiValues;
    }

    public String getTiersType() {
        return tiersType;
    }

    public void setTiersType(String tiersType) {
        this.tiersType = tiersType;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<String> getMails() {
        return mails;
    }

    public void setMails(List<String> mails) {
        this.mails = mails;
    }

    public String getMailRecipient() {
        return mailRecipient;
    }

    public void setMailRecipient(String mailRecipient) {
        this.mailRecipient = mailRecipient;
    }

    public BigDecimal getRffFormaliteRate() {
        return rffFormaliteRate;
    }

    public void setRffFormaliteRate(BigDecimal rffFormaliteRate) {
        this.rffFormaliteRate = rffFormaliteRate;
    }

    public BigDecimal getRffInsertionRate() {
        return rffInsertionRate;
    }

    public void setRffInsertionRate(BigDecimal rffInsertionRate) {
        this.rffInsertionRate = rffInsertionRate;
    }

    public List<String> getSpecialOffers() {
        return specialOffers;
    }

    public void setSpecialOffers(List<String> specialOffers) {
        this.specialOffers = specialOffers;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<String> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(List<String> competitors) {
        this.competitors = competitors;
    }

    public String getAccountingAccountCustomer() {
        return accountingAccountCustomer;
    }

    public void setAccountingAccountCustomer(String accountingAccountCustomer) {
        this.accountingAccountCustomer = accountingAccountCustomer;
    }

    public String getAccountingAccountDeposit() {
        return accountingAccountDeposit;
    }

    public void setAccountingAccountDeposit(String accountingAccountDeposit) {
        this.accountingAccountDeposit = accountingAccountDeposit;
    }
}
