package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ModalitesDeControle;

@Entity
public class Modalite implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private LocalDate dateEffet;

    private Boolean detentionPartDirecte;

    private Integer partsDirectesPleinePropriete;

    private Integer partsDirectesNuePropriete;

    private Boolean detentionPartIndirecte;

    private Integer partsIndirectesIndivision;

    private Integer partsIndirectesIndivisionPleinePropriete;

    private Integer partsIndirectesIndivisionNuePropriete;

    private Integer partsIndirectesPersonnesMorales;

    private Integer partsIndirectesPmoralesPleinePropriete;

    private Integer partsIndirectesPmoralesNuePropriete;

    private Integer detentionPartTotale;

    private Boolean detentionVoteDirecte;

    private Integer voteDirectePleinePropriete;

    private Integer voteDirecteNuePropriete;

    private Integer voteDirecteUsufruit;

    private Boolean detentionVoteIndirecte;

    private Integer voteIndirecteIndivision;

    private Integer voteIndirecteIndivisionPleinePropriete;

    private Integer voteIndirecteIndivisionNuePropriete;

    private Integer voteIndirecteIndivisionUsufruit;

    private Integer voteIndirectePersonnesMorales;

    private Integer voteIndirectePmoralesPleinePropriete;

    private Integer voteIndirectePmoralesNuePropriete;

    private Integer voteIndirectePmoralesUsufruit;

    private Integer vocationTitulaireDirectePleinePropriete;

    private Integer vocationTitulaireDirecteNuePropriete;

    private Integer vocationTitulaireIndirecteIndivision;

    private Integer vocationTitulaireIndirectePleinePropriete;

    private Integer vocationTitulaireIndirecteNuePropriete;

    private Integer vocationTitulaireIndirectePersonnesMorales;

    private Integer vocationTitulaireIndirectePmoralesPleinePropriete;

    private Integer vocationTitulaireIndirectePmoralesNuePropriete;

    private Integer detentionVoteTotal;

    private Boolean detentionPouvoirDecisionAG;

    private Boolean detentionPouvoirNommageMembresConseilAdmin;

    private Boolean detentionAutresMoyensControle;

    private Boolean beneficiaireRepresentantLegal;

    private Boolean gestionDelegueeSocieteGestion;

    private Boolean representantLegalPlacementSansGestionDelegue;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "asso_modalite_modalite_de_controle", joinColumns = @JoinColumn(name = "id_modalite"), inverseJoinColumns = @JoinColumn(name = "code_modalite_de_controle"))
    private List<ModalitesDeControle> modalitesDeControle;

    private Boolean detention25pCapital;

    private Boolean detention25pDroitVote;

    private Boolean detentionCapitalIndirecteIndivision;

    private Boolean detentionCapitalIndirectesPersonnesMorales;

    private Boolean detentionVoteIndirecteIndivision;

    private Boolean detentionVoteIndirectePersonnesMorales;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_societe_gestion")
    Entreprise societeGestion;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_domicile")
    AdresseDomicile adresse;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public Boolean getDetentionPartDirecte() {
        return detentionPartDirecte;
    }

    public void setDetentionPartDirecte(Boolean detentionPartDirecte) {
        this.detentionPartDirecte = detentionPartDirecte;
    }

    public Integer getPartsDirectesPleinePropriete() {
        return partsDirectesPleinePropriete;
    }

    public void setPartsDirectesPleinePropriete(Integer partsDirectesPleinePropriete) {
        this.partsDirectesPleinePropriete = partsDirectesPleinePropriete;
    }

    public Integer getPartsDirectesNuePropriete() {
        return partsDirectesNuePropriete;
    }

    public void setPartsDirectesNuePropriete(Integer partsDirectesNuePropriete) {
        this.partsDirectesNuePropriete = partsDirectesNuePropriete;
    }

    public Boolean getDetentionPartIndirecte() {
        return detentionPartIndirecte;
    }

    public void setDetentionPartIndirecte(Boolean detentionPartIndirecte) {
        this.detentionPartIndirecte = detentionPartIndirecte;
    }

    public Integer getPartsIndirectesIndivision() {
        return partsIndirectesIndivision;
    }

    public void setPartsIndirectesIndivision(Integer partsIndirectesIndivision) {
        this.partsIndirectesIndivision = partsIndirectesIndivision;
    }

    public Integer getPartsIndirectesIndivisionPleinePropriete() {
        return partsIndirectesIndivisionPleinePropriete;
    }

    public void setPartsIndirectesIndivisionPleinePropriete(Integer partsIndirectesIndivisionPleinePropriete) {
        this.partsIndirectesIndivisionPleinePropriete = partsIndirectesIndivisionPleinePropriete;
    }

    public Integer getPartsIndirectesIndivisionNuePropriete() {
        return partsIndirectesIndivisionNuePropriete;
    }

    public void setPartsIndirectesIndivisionNuePropriete(Integer partsIndirectesIndivisionNuePropriete) {
        this.partsIndirectesIndivisionNuePropriete = partsIndirectesIndivisionNuePropriete;
    }

    public Integer getPartsIndirectesPersonnesMorales() {
        return partsIndirectesPersonnesMorales;
    }

    public void setPartsIndirectesPersonnesMorales(Integer partsIndirectesPersonnesMorales) {
        this.partsIndirectesPersonnesMorales = partsIndirectesPersonnesMorales;
    }

    public Integer getPartsIndirectesPmoralesPleinePropriete() {
        return partsIndirectesPmoralesPleinePropriete;
    }

    public void setPartsIndirectesPmoralesPleinePropriete(Integer partsIndirectesPmoralesPleinePropriete) {
        this.partsIndirectesPmoralesPleinePropriete = partsIndirectesPmoralesPleinePropriete;
    }

    public Integer getPartsIndirectesPmoralesNuePropriete() {
        return partsIndirectesPmoralesNuePropriete;
    }

    public void setPartsIndirectesPmoralesNuePropriete(Integer partsIndirectesPmoralesNuePropriete) {
        this.partsIndirectesPmoralesNuePropriete = partsIndirectesPmoralesNuePropriete;
    }

    public Integer getDetentionPartTotale() {
        return detentionPartTotale;
    }

    public void setDetentionPartTotale(Integer detentionPartTotale) {
        this.detentionPartTotale = detentionPartTotale;
    }

    public Boolean getDetentionVoteDirecte() {
        return detentionVoteDirecte;
    }

    public void setDetentionVoteDirecte(Boolean detentionVoteDirecte) {
        this.detentionVoteDirecte = detentionVoteDirecte;
    }

    public Integer getVoteDirectePleinePropriete() {
        return voteDirectePleinePropriete;
    }

    public void setVoteDirectePleinePropriete(Integer voteDirectePleinePropriete) {
        this.voteDirectePleinePropriete = voteDirectePleinePropriete;
    }

    public Integer getVoteDirecteNuePropriete() {
        return voteDirecteNuePropriete;
    }

    public void setVoteDirecteNuePropriete(Integer voteDirecteNuePropriete) {
        this.voteDirecteNuePropriete = voteDirecteNuePropriete;
    }

    public Integer getVoteDirecteUsufruit() {
        return voteDirecteUsufruit;
    }

    public void setVoteDirecteUsufruit(Integer voteDirecteUsufruit) {
        this.voteDirecteUsufruit = voteDirecteUsufruit;
    }

    public Boolean getDetentionVoteIndirecte() {
        return detentionVoteIndirecte;
    }

    public void setDetentionVoteIndirecte(Boolean detentionVoteIndirecte) {
        this.detentionVoteIndirecte = detentionVoteIndirecte;
    }

    public Integer getVoteIndirecteIndivision() {
        return voteIndirecteIndivision;
    }

    public void setVoteIndirecteIndivision(Integer voteIndirecteIndivision) {
        this.voteIndirecteIndivision = voteIndirecteIndivision;
    }

    public Integer getVoteIndirecteIndivisionPleinePropriete() {
        return voteIndirecteIndivisionPleinePropriete;
    }

    public void setVoteIndirecteIndivisionPleinePropriete(Integer voteIndirecteIndivisionPleinePropriete) {
        this.voteIndirecteIndivisionPleinePropriete = voteIndirecteIndivisionPleinePropriete;
    }

    public Integer getVoteIndirecteIndivisionNuePropriete() {
        return voteIndirecteIndivisionNuePropriete;
    }

    public void setVoteIndirecteIndivisionNuePropriete(Integer voteIndirecteIndivisionNuePropriete) {
        this.voteIndirecteIndivisionNuePropriete = voteIndirecteIndivisionNuePropriete;
    }

    public Integer getVoteIndirecteIndivisionUsufruit() {
        return voteIndirecteIndivisionUsufruit;
    }

    public void setVoteIndirecteIndivisionUsufruit(Integer voteIndirecteIndivisionUsufruit) {
        this.voteIndirecteIndivisionUsufruit = voteIndirecteIndivisionUsufruit;
    }

    public Integer getVoteIndirectePersonnesMorales() {
        return voteIndirectePersonnesMorales;
    }

    public void setVoteIndirectePersonnesMorales(Integer voteIndirectePersonnesMorales) {
        this.voteIndirectePersonnesMorales = voteIndirectePersonnesMorales;
    }

    public Integer getVoteIndirectePmoralesPleinePropriete() {
        return voteIndirectePmoralesPleinePropriete;
    }

    public void setVoteIndirectePmoralesPleinePropriete(Integer voteIndirectePmoralesPleinePropriete) {
        this.voteIndirectePmoralesPleinePropriete = voteIndirectePmoralesPleinePropriete;
    }

    public Integer getVoteIndirectePmoralesNuePropriete() {
        return voteIndirectePmoralesNuePropriete;
    }

    public void setVoteIndirectePmoralesNuePropriete(Integer voteIndirectePmoralesNuePropriete) {
        this.voteIndirectePmoralesNuePropriete = voteIndirectePmoralesNuePropriete;
    }

    public Integer getVoteIndirectePmoralesUsufruit() {
        return voteIndirectePmoralesUsufruit;
    }

    public void setVoteIndirectePmoralesUsufruit(Integer voteIndirectePmoralesUsufruit) {
        this.voteIndirectePmoralesUsufruit = voteIndirectePmoralesUsufruit;
    }

    public Integer getVocationTitulaireDirectePleinePropriete() {
        return vocationTitulaireDirectePleinePropriete;
    }

    public void setVocationTitulaireDirectePleinePropriete(Integer vocationTitulaireDirectePleinePropriete) {
        this.vocationTitulaireDirectePleinePropriete = vocationTitulaireDirectePleinePropriete;
    }

    public Integer getVocationTitulaireDirecteNuePropriete() {
        return vocationTitulaireDirecteNuePropriete;
    }

    public void setVocationTitulaireDirecteNuePropriete(Integer vocationTitulaireDirecteNuePropriete) {
        this.vocationTitulaireDirecteNuePropriete = vocationTitulaireDirecteNuePropriete;
    }

    public Integer getVocationTitulaireIndirecteIndivision() {
        return vocationTitulaireIndirecteIndivision;
    }

    public void setVocationTitulaireIndirecteIndivision(Integer vocationTitulaireIndirecteIndivision) {
        this.vocationTitulaireIndirecteIndivision = vocationTitulaireIndirecteIndivision;
    }

    public Integer getVocationTitulaireIndirectePleinePropriete() {
        return vocationTitulaireIndirectePleinePropriete;
    }

    public void setVocationTitulaireIndirectePleinePropriete(Integer vocationTitulaireIndirectePleinePropriete) {
        this.vocationTitulaireIndirectePleinePropriete = vocationTitulaireIndirectePleinePropriete;
    }

    public Integer getVocationTitulaireIndirecteNuePropriete() {
        return vocationTitulaireIndirecteNuePropriete;
    }

    public void setVocationTitulaireIndirecteNuePropriete(Integer vocationTitulaireIndirecteNuePropriete) {
        this.vocationTitulaireIndirecteNuePropriete = vocationTitulaireIndirecteNuePropriete;
    }

    public Integer getVocationTitulaireIndirectePersonnesMorales() {
        return vocationTitulaireIndirectePersonnesMorales;
    }

    public void setVocationTitulaireIndirectePersonnesMorales(Integer vocationTitulaireIndirectePersonnesMorales) {
        this.vocationTitulaireIndirectePersonnesMorales = vocationTitulaireIndirectePersonnesMorales;
    }

    public Integer getVocationTitulaireIndirectePmoralesPleinePropriete() {
        return vocationTitulaireIndirectePmoralesPleinePropriete;
    }

    public void setVocationTitulaireIndirectePmoralesPleinePropriete(
            Integer vocationTitulaireIndirectePmoralesPleinePropriete) {
        this.vocationTitulaireIndirectePmoralesPleinePropriete = vocationTitulaireIndirectePmoralesPleinePropriete;
    }

    public Integer getVocationTitulaireIndirectePmoralesNuePropriete() {
        return vocationTitulaireIndirectePmoralesNuePropriete;
    }

    public void setVocationTitulaireIndirectePmoralesNuePropriete(
            Integer vocationTitulaireIndirectePmoralesNuePropriete) {
        this.vocationTitulaireIndirectePmoralesNuePropriete = vocationTitulaireIndirectePmoralesNuePropriete;
    }

    public Integer getDetentionVoteTotal() {
        return detentionVoteTotal;
    }

    public void setDetentionVoteTotal(Integer detentionVoteTotal) {
        this.detentionVoteTotal = detentionVoteTotal;
    }

    public Boolean getDetentionPouvoirDecisionAG() {
        return detentionPouvoirDecisionAG;
    }

    public void setDetentionPouvoirDecisionAG(Boolean detentionPouvoirDecisionAG) {
        this.detentionPouvoirDecisionAG = detentionPouvoirDecisionAG;
    }

    public Boolean getDetentionPouvoirNommageMembresConseilAdmin() {
        return detentionPouvoirNommageMembresConseilAdmin;
    }

    public void setDetentionPouvoirNommageMembresConseilAdmin(Boolean detentionPouvoirNommageMembresConseilAdmin) {
        this.detentionPouvoirNommageMembresConseilAdmin = detentionPouvoirNommageMembresConseilAdmin;
    }

    public Boolean getDetentionAutresMoyensControle() {
        return detentionAutresMoyensControle;
    }

    public void setDetentionAutresMoyensControle(Boolean detentionAutresMoyensControle) {
        this.detentionAutresMoyensControle = detentionAutresMoyensControle;
    }

    public Boolean getBeneficiaireRepresentantLegal() {
        return beneficiaireRepresentantLegal;
    }

    public void setBeneficiaireRepresentantLegal(Boolean beneficiaireRepresentantLegal) {
        this.beneficiaireRepresentantLegal = beneficiaireRepresentantLegal;
    }

    public Boolean getGestionDelegueeSocieteGestion() {
        return gestionDelegueeSocieteGestion;
    }

    public void setGestionDelegueeSocieteGestion(Boolean gestionDelegueeSocieteGestion) {
        this.gestionDelegueeSocieteGestion = gestionDelegueeSocieteGestion;
    }

    public Boolean getRepresentantLegalPlacementSansGestionDelegue() {
        return representantLegalPlacementSansGestionDelegue;
    }

    public void setRepresentantLegalPlacementSansGestionDelegue(Boolean representantLegalPlacementSansGestionDelegue) {
        this.representantLegalPlacementSansGestionDelegue = representantLegalPlacementSansGestionDelegue;
    }

    public Boolean getDetention25pCapital() {
        return detention25pCapital;
    }

    public void setDetention25pCapital(Boolean detention25pCapital) {
        this.detention25pCapital = detention25pCapital;
    }

    public Boolean getDetention25pDroitVote() {
        return detention25pDroitVote;
    }

    public void setDetention25pDroitVote(Boolean detention25pDroitVote) {
        this.detention25pDroitVote = detention25pDroitVote;
    }

    public Boolean getDetentionCapitalIndirecteIndivision() {
        return detentionCapitalIndirecteIndivision;
    }

    public void setDetentionCapitalIndirecteIndivision(Boolean detentionCapitalIndirecteIndivision) {
        this.detentionCapitalIndirecteIndivision = detentionCapitalIndirecteIndivision;
    }

    public Boolean getDetentionCapitalIndirectesPersonnesMorales() {
        return detentionCapitalIndirectesPersonnesMorales;
    }

    public void setDetentionCapitalIndirectesPersonnesMorales(Boolean detentionCapitalIndirectesPersonnesMorales) {
        this.detentionCapitalIndirectesPersonnesMorales = detentionCapitalIndirectesPersonnesMorales;
    }

    public Boolean getDetentionVoteIndirecteIndivision() {
        return detentionVoteIndirecteIndivision;
    }

    public void setDetentionVoteIndirecteIndivision(Boolean detentionVoteIndirecteIndivision) {
        this.detentionVoteIndirecteIndivision = detentionVoteIndirecteIndivision;
    }

    public Boolean getDetentionVoteIndirectePersonnesMorales() {
        return detentionVoteIndirectePersonnesMorales;
    }

    public void setDetentionVoteIndirectePersonnesMorales(Boolean detentionVoteIndirectePersonnesMorales) {
        this.detentionVoteIndirectePersonnesMorales = detentionVoteIndirectePersonnesMorales;
    }

    public Entreprise getSocieteGestion() {
        return societeGestion;
    }

    public void setSocieteGestion(Entreprise societeGestion) {
        this.societeGestion = societeGestion;
    }

    public AdresseDomicile getAdresse() {
        return adresse;
    }

    public void setAdresse(AdresseDomicile adresse) {
        this.adresse = adresse;
    }

    public List<ModalitesDeControle> getModalitesDeControle() {
        return modalitesDeControle;
    }

    public void setModalitesDeControle(List<ModalitesDeControle> modalitesDeControle) {
        this.modalitesDeControle = modalitesDeControle;
    }

}
