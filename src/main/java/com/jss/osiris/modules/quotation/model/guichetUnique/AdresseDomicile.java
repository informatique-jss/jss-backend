package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodePays;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeVoie;

@Entity
public class AdresseDomicile implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(length = 255)
    private String roleAdresse;

    @Column(length = 255)
    private String pays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_code_pays")
    CodePays codePays;

    @Column(length = 255)
    private String codePostal;

    @Column(length = 255)
    private String commune;

    @Column(length = 255)
    private String codeInseeCommune;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_voie")
    TypeVoie typeVoie;

    @Column(length = 255)
    private String voie;

    @Column(length = 255)
    private String voieCodifiee;

    @Column(length = 255)
    private String numVoie;

    @Column(length = 255)
    private String indiceRepetition;

    @Column(length = 255)
    private String distributionSpeciale;

    @Column(length = 255)
    private String communeAncienne;

    @Column(length = 255)
    private String rgpd;

    @Column()
    private LocalDate datePriseEffetAdresse;

    @Column(length = 255)
    private String complementLocalisation;

    @Column(length = 255)
    private String communeDeRattachement;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caracteristiques")
    Caracteristiques caracteristiques;

    @Column()
    private Boolean indicateurValidationBAN;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleAdresse() {
        return roleAdresse;
    }

    public void setRoleAdresse(String roleAdresse) {
        this.roleAdresse = roleAdresse;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public CodePays getCodePays() {
        return codePays;
    }

    public void setCodePays(CodePays codePays) {
        this.codePays = codePays;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getCodeInseeCommune() {
        return codeInseeCommune;
    }

    public void setCodeInseeCommune(String codeInseeCommune) {
        this.codeInseeCommune = codeInseeCommune;
    }

    public TypeVoie getTypeVoie() {
        return typeVoie;
    }

    public void setTypeVoie(TypeVoie typeVoie) {
        this.typeVoie = typeVoie;
    }

    public String getVoie() {
        return voie;
    }

    public void setVoie(String voie) {
        this.voie = voie;
    }

    public String getVoieCodifiee() {
        return voieCodifiee;
    }

    public void setVoieCodifiee(String voieCodifiee) {
        this.voieCodifiee = voieCodifiee;
    }

    public String getNumVoie() {
        return numVoie;
    }

    public void setNumVoie(String numVoie) {
        this.numVoie = numVoie;
    }

    public String getIndiceRepetition() {
        return indiceRepetition;
    }

    public void setIndiceRepetition(String indiceRepetition) {
        this.indiceRepetition = indiceRepetition;
    }

    public String getDistributionSpeciale() {
        return distributionSpeciale;
    }

    public void setDistributionSpeciale(String distributionSpeciale) {
        this.distributionSpeciale = distributionSpeciale;
    }

    public String getCommuneAncienne() {
        return communeAncienne;
    }

    public void setCommuneAncienne(String communeAncienne) {
        this.communeAncienne = communeAncienne;
    }

    public String getRgpd() {
        return rgpd;
    }

    public void setRgpd(String rgpd) {
        this.rgpd = rgpd;
    }

    public LocalDate getDatePriseEffetAdresse() {
        return datePriseEffetAdresse;
    }

    public void setDatePriseEffetAdresse(LocalDate datePriseEffetAdresse) {
        this.datePriseEffetAdresse = datePriseEffetAdresse;
    }

    public String getComplementLocalisation() {
        return complementLocalisation;
    }

    public void setComplementLocalisation(String complementLocalisation) {
        this.complementLocalisation = complementLocalisation;
    }

    public String getCommuneDeRattachement() {
        return communeDeRattachement;
    }

    public void setCommuneDeRattachement(String communeDeRattachement) {
        this.communeDeRattachement = communeDeRattachement;
    }

    public Caracteristiques getCaracteristiques() {
        return caracteristiques;
    }

    public void setCaracteristiques(Caracteristiques caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public Boolean getIndicateurValidationBAN() {
        return indicateurValidationBAN;
    }

    public void setIndicateurValidationBAN(Boolean indicateurValidationBAN) {
        this.indicateurValidationBAN = indicateurValidationBAN;
    }

}
