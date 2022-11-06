package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class PersonnePhysique implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_identite", nullable = false)
    Identite identite;

    @ManyToOne
    @JoinColumn(name = "id_adresse_entreprise", nullable = false)
    AdresseEntreprise adresseEntreprise;

    @ManyToOne
    @JoinColumn(name = "id_composition", nullable = false)
    Composition composition;

    @ManyToOne
    @JoinColumn(name = "id_etablissement_principal", nullable = false)
    EtablissementPrincipal etablissementPrincipal;

    @OneToMany(mappedBy = "personnePhysique")
    @JsonIgnoreProperties(value = { "personnePhysique" }, allowSetters = true)
    List<AutresEtablissement> autresEtablissements;

    @ManyToOne
    @JoinColumn(name = "id_options_fiscales", nullable = false)
    OptionsFiscales optionsFiscales;

    @ManyToOne
    @JoinColumn(name = "id_detail_cessation_entreprise", nullable = false)
    DetailCessationEntreprise detailCessationEntreprise;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Identite getIdentite() {
        return identite;
    }

    public void setIdentite(Identite identite) {
        this.identite = identite;
    }

    public AdresseEntreprise getAdresseEntreprise() {
        return adresseEntreprise;
    }

    public void setAdresseEntreprise(AdresseEntreprise adresseEntreprise) {
        this.adresseEntreprise = adresseEntreprise;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public EtablissementPrincipal getEtablissementPrincipal() {
        return etablissementPrincipal;
    }

    public void setEtablissementPrincipal(EtablissementPrincipal etablissementPrincipal) {
        this.etablissementPrincipal = etablissementPrincipal;
    }

    public OptionsFiscales getOptionsFiscales() {
        return optionsFiscales;
    }

    public void setOptionsFiscales(OptionsFiscales optionsFiscales) {
        this.optionsFiscales = optionsFiscales;
    }

    public DetailCessationEntreprise getDetailCessationEntreprise() {
        return detailCessationEntreprise;
    }

    public void setDetailCessationEntreprise(DetailCessationEntreprise detailCessationEntreprise) {
        this.detailCessationEntreprise = detailCessationEntreprise;
    }

    public List<AutresEtablissement> getAutresEtablissements() {
        return autresEtablissements;
    }

    public void setAutresEtablissements(List<AutresEtablissement> autresEtablissements) {
        this.autresEtablissements = autresEtablissements;
    }

}
