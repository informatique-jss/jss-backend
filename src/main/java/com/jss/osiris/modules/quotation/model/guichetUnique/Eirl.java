package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifFinEirl;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionEirl;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegistreEirl;

@Entity
public class Eirl implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_identite")
    @JsonIgnoreProperties(value = { "eirl" }, allowSetters = true)
    Identite identite;

    @ManyToOne
    @JoinColumn(name = "id_option_eirl", nullable = false)
    OptionEirl optionEirl;

    @ManyToOne
    @JoinColumn(name = "id_registre_eirl", nullable = false)
    RegistreEirl registreEirl;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @Column(nullable = false)
    private Boolean indicateurDeclarationComplementaireAffectationPatrimoine;

    @ManyToOne
    @JoinColumn(name = "id_motif_fin_eirl", nullable = false)
    MotifFinEirl motifFinEirl;

    @Column(nullable = false)
    private Boolean intentionDePoursuite;

    @Column(nullable = false, length = 255)
    private String denomination;

    @Column(nullable = false, length = 255)
    private String objet;

    @ManyToOne
    @JoinColumn(name = "id_ancienne_eirl", nullable = false)
    AncienneEIRL ancienneEIRL;

    @ManyToOne
    @JoinColumn(name = "id_option_fiscale", nullable = false)
    OptionFiscale optionFiscale;

    @ManyToOne
    @JoinColumn(name = "id_repreneur", nullable = false)
    Repreneur repreneur;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OptionEirl getOptionEirl() {
        return optionEirl;
    }

    public void setOptionEirl(OptionEirl optionEirl) {
        this.optionEirl = optionEirl;
    }

    public RegistreEirl getRegistreEirl() {
        return registreEirl;
    }

    public void setRegistreEirl(RegistreEirl registreEirl) {
        this.registreEirl = registreEirl;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public Boolean getIndicateurDeclarationComplementaireAffectationPatrimoine() {
        return indicateurDeclarationComplementaireAffectationPatrimoine;
    }

    public void setIndicateurDeclarationComplementaireAffectationPatrimoine(
            Boolean indicateurDeclarationComplementaireAffectationPatrimoine) {
        this.indicateurDeclarationComplementaireAffectationPatrimoine = indicateurDeclarationComplementaireAffectationPatrimoine;
    }

    public MotifFinEirl getMotifFinEirl() {
        return motifFinEirl;
    }

    public void setMotifFinEirl(MotifFinEirl motifFinEirl) {
        this.motifFinEirl = motifFinEirl;
    }

    public Boolean getIntentionDePoursuite() {
        return intentionDePoursuite;
    }

    public void setIntentionDePoursuite(Boolean intentionDePoursuite) {
        this.intentionDePoursuite = intentionDePoursuite;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public AncienneEIRL getAncienneEIRL() {
        return ancienneEIRL;
    }

    public void setAncienneEIRL(AncienneEIRL ancienneEIRL) {
        this.ancienneEIRL = ancienneEIRL;
    }

    public OptionFiscale getOptionFiscale() {
        return optionFiscale;
    }

    public void setOptionFiscale(OptionFiscale optionFiscale) {
        this.optionFiscale = optionFiscale;
    }

    public Repreneur getRepreneur() {
        return repreneur;
    }

    public void setRepreneur(Repreneur repreneur) {
        this.repreneur = repreneur;
    }

    public Identite getIdentite() {
        return identite;
    }

    public void setIdentite(Identite identite) {
        this.identite = identite;
    }

}
