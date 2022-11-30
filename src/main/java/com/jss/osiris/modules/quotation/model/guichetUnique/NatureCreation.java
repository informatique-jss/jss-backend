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

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeExploitation;

@Entity
public class NatureCreation implements Serializable, IId {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Boolean societeEtrangere;

    @ManyToOne
    @JoinColumn(name = "id_forme_juridique", nullable = false)
    FormeJuridique formeJuridique;

    @ManyToOne
    @JoinColumn(name = "id_type_exploitation")
    TypeExploitation typeExploitation;

    @Column(nullable = false)
    private Boolean microEntreprise;

    @Column(nullable = false)
    private Boolean etablieEnFrance;

    @Column(nullable = false)
    private Boolean salarieEnFrance;

    @Column(nullable = false)
    private Boolean relieeEntrepriseAgricole;

    @Column(nullable = false)
    private Boolean entrepriseAgricole;

    @Column(nullable = false)
    private Boolean eirl;

    @Column(nullable = false)
    private Boolean indicateurEtablissementFictif;

    @Column(nullable = false)
    private Boolean seulsBeneficiairesModifies;

    @Column(nullable = false)
    private LocalDate dateDepotCreation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getSocieteEtrangere() {
        return societeEtrangere;
    }

    public void setSocieteEtrangere(Boolean societeEtrangere) {
        this.societeEtrangere = societeEtrangere;
    }

    public FormeJuridique getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(FormeJuridique formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public TypeExploitation getTypeExploitation() {
        return typeExploitation;
    }

    public void setTypeExploitation(TypeExploitation typeExploitation) {
        this.typeExploitation = typeExploitation;
    }

    public Boolean getMicroEntreprise() {
        return microEntreprise;
    }

    public void setMicroEntreprise(Boolean microEntreprise) {
        this.microEntreprise = microEntreprise;
    }

    public Boolean getEtablieEnFrance() {
        return etablieEnFrance;
    }

    public void setEtablieEnFrance(Boolean etablieEnFrance) {
        this.etablieEnFrance = etablieEnFrance;
    }

    public Boolean getSalarieEnFrance() {
        return salarieEnFrance;
    }

    public void setSalarieEnFrance(Boolean salarieEnFrance) {
        this.salarieEnFrance = salarieEnFrance;
    }

    public Boolean getRelieeEntrepriseAgricole() {
        return relieeEntrepriseAgricole;
    }

    public void setRelieeEntrepriseAgricole(Boolean relieeEntrepriseAgricole) {
        this.relieeEntrepriseAgricole = relieeEntrepriseAgricole;
    }

    public Boolean getEntrepriseAgricole() {
        return entrepriseAgricole;
    }

    public void setEntrepriseAgricole(Boolean entrepriseAgricole) {
        this.entrepriseAgricole = entrepriseAgricole;
    }

    public Boolean getEirl() {
        return eirl;
    }

    public void setEirl(Boolean eirl) {
        this.eirl = eirl;
    }

    public Boolean getIndicateurEtablissementFictif() {
        return indicateurEtablissementFictif;
    }

    public void setIndicateurEtablissementFictif(Boolean indicateurEtablissementFictif) {
        this.indicateurEtablissementFictif = indicateurEtablissementFictif;
    }

    public Boolean getSeulsBeneficiairesModifies() {
        return seulsBeneficiairesModifies;
    }

    public void setSeulsBeneficiairesModifies(Boolean seulsBeneficiairesModifies) {
        this.seulsBeneficiairesModifies = seulsBeneficiairesModifies;
    }

    public LocalDate getDateDepotCreation() {
        return dateDepotCreation;
    }

    public void setDateDepotCreation(LocalDate dateDepotCreation) {
        this.dateDepotCreation = dateDepotCreation;
    }

}
