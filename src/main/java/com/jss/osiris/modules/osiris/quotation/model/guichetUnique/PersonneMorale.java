package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties
public class PersonneMorale implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_identite")
    Identite identite;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_entreprise")
    AdresseEntreprise adresseEntreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_composition")
    Composition composition;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etablissement_principal")
    EtablissementPrincipal etablissementPrincipal;

    @OneToMany(mappedBy = "personneMorale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "personneMorale" }, allowSetters = true)
    List<AutresEtablissement> autresEtablissements;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_options_fiscales")
    OptionsFiscales optionsFiscales;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detail_cessation_entreprise")
    DetailCessationEntreprise detailCessationEntreprise;

    @OneToMany(mappedBy = "personneMorale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "personneMorale" }, allowSetters = true)
    List<BeneficiairesEffectif> beneficiairesEffectifs;

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

    public List<BeneficiairesEffectif> getBeneficiairesEffectifs() {
        return beneficiairesEffectifs;
    }

    public void setBeneficiairesEffectifs(List<BeneficiairesEffectif> beneficiairesEffectifs) {
        this.beneficiairesEffectifs = beneficiairesEffectifs;
    }

}
