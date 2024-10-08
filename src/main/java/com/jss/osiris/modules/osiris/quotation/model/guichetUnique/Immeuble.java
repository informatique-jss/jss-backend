package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.CodePays;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeVoie;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@DoNotAudit
@Table(indexes = {
        @Index(name = "idx_immeuble_option_fiscale", columnList = "id_option_fiscale"),
        @Index(name = "idx_immeuble_option_fiscales", columnList = "id_options_fiscales") })
public class Immeuble implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_option_fiscale")
    @JsonIgnoreProperties(value = { "immeubles" }, allowSetters = true)
    OptionFiscale optionFiscale;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_options_fiscales")
    @JsonIgnoreProperties(value = { "immeubles" }, allowSetters = true)
    OptionsFiscales optionsFiscales;

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

    private LocalDate datePriseEffetAdresse;

    @Column(length = 255)
    private String complementLocalisation;

    @Column(length = 255)
    private String communeDeRattachement;

    @Column(length = 255)
    String caracteristiques;

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

    public Boolean getIndicateurValidationBAN() {
        return indicateurValidationBAN;
    }

    public void setIndicateurValidationBAN(Boolean indicateurValidationBAN) {
        this.indicateurValidationBAN = indicateurValidationBAN;
    }

    public OptionFiscale getOptionFiscale() {
        return optionFiscale;
    }

    public void setOptionFiscale(OptionFiscale optionFiscale) {
        this.optionFiscale = optionFiscale;
    }

    public OptionsFiscales getOptionsFiscales() {
        return optionsFiscales;
    }

    public void setOptionsFiscales(OptionsFiscales optionsFiscales) {
        this.optionsFiscales = optionsFiscales;
    }

    public String getCaracteristiques() {
        return caracteristiques;
    }

    public void setCaracteristiques(String caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

}
