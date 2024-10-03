package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.MotifFinEirl;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OptionEirl;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RegistreEirl;

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
        @Index(name = "idx_eirl_identite", columnList = "id_identite") })
public class Eirl implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_identite")
    @JsonIgnoreProperties(value = { "eirl" }, allowSetters = true)
    Identite identite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_option_eirl")
    OptionEirl optionEirl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registre_eirl")
    RegistreEirl registreEirl;

    private LocalDate dateEffet;

    private Boolean indicateurDeclarationComplementaireAffectationPatrimoine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motif_fin_eirl")
    MotifFinEirl motifFinEirl;

    private Boolean intentionDePoursuite;

    @Column(length = 255)
    private String denomination;

    @Column(length = 255)
    private String objet;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ancienne_eirl")
    AncienneEIRL ancienneEIRL;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_option_fiscale")
    OptionFiscale optionFiscale;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_repreneur")
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
