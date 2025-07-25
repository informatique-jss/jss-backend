package com.jss.osiris.modules.osiris.beneficialOwner.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.Country;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class BeneficialOwner implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "beneficial_owner_sequence", sequenceName = "beneficial_owner_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "beneficial_owner_sequence")
    private Integer id;

    private String birthName;
    private String usedName;
    private String firstNames;
    private String nationality;
    private LocalDate birthDate;
    private String residenceAddress;
    private String postalCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_city")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_country")
    private Country country;

    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_affaire")
    @JsonIgnoreProperties(value = { "beneficialOwners" }, allowSetters = true)
    private Affaire affaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite")
    @JsonIgnoreProperties(value = { "beneficialOwners" }, allowSetters = true)
    private Formalite formalite;

    @Embedded
    private ShareHolding shareHolding;
    @Embedded
    private VotingRights votingRights;
    @Embedded
    private OtherControls otherControls;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBirthName() {
        return birthName;
    }

    public void setBirthName(String birthName) {
        this.birthName = birthName;
    }

    public String getUsedName() {
        return usedName;
    }

    public void setUsedName(String usedName) {
        this.usedName = usedName;
    }

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(String firstNames) {
        this.firstNames = firstNames;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getResidenceAddress() {
        return residenceAddress;
    }

    public void setResidenceAddress(String residenceAddress) {
        this.residenceAddress = residenceAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public ShareHolding getShareHolding() {
        return shareHolding;
    }

    public void setShareHolding(ShareHolding shareHolding) {
        this.shareHolding = shareHolding;
    }

    public VotingRights getVotingRights() {
        return votingRights;
    }

    public void setVotingRights(VotingRights votingRights) {
        this.votingRights = votingRights;
    }

    public OtherControls getOtherControls() {
        return otherControls;
    }

    public void setOtherControls(OtherControls otherControls) {
        this.otherControls = otherControls;
    }

    public Affaire getAffaire() {
        return affaire;
    }

    public void setAffaire(Affaire affaire) {
        this.affaire = affaire;
    }

    public Formalite getFormalite() {
        return formalite;
    }

    public void setFormalite(Formalite formalite) {
        this.formalite = formalite;
    }

}
