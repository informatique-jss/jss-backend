package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.Destination;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.LieuDeLiquidation;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.MotifCessation;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.MotifDisparition;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDissolution;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailCessationEntreprise implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @OneToMany(mappedBy = "detailCessationEntreprise")
    @JsonIgnoreProperties(value = { "detailCessationEntreprise" }, allowSetters = true)
    List<Pouvoir> repreneurs;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_domicile")
    AdresseDomicile adresse;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_preneur_bail")
    PreneurBail preneurBail;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_preneur_bail")
    AdresseDomicile adressePreneurBail;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_publicite_nomination_liquidateur")
    Publication publiciteNominationLiquidateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motif_cessation")
    MotifCessation motifCessation;

    private Boolean maintienRcs;

    private Boolean maintienRm;

    private LocalDate dateCessationActiviteSalariee;

    private LocalDate dateEffet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lieu_de_liquidation")
    LieuDeLiquidation lieuDeLiquidation;

    private LocalDate dateCessationTotaleActivite;

    private LocalDate dateClotureLiquidation;

    private LocalDate dateTransfertPatrimoine;

    private LocalDate dateDissolutionDisparition;

    private Boolean indicateurCessationTemporaire;

    private Boolean indicateurDecesEntrepreneur;

    private Boolean indicateurPoursuiteActivite;

    private Boolean indicateurMaintienImmatriculationRegistre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destination")
    Destination destination;

    private Boolean indicateurDissolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_dissolution")
    TypeDissolution typeDissolution;

    private Boolean indicateurDisparitionPM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motif_disparition")
    MotifDisparition motifDisparition;

    private Boolean indicateurPresenceSalarie;

    private LocalDate dateMiseEnSommeil;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AdresseDomicile getAdresse() {
        return adresse;
    }

    public void setAdresse(AdresseDomicile adresse) {
        this.adresse = adresse;
    }

    public PreneurBail getPreneurBail() {
        return preneurBail;
    }

    public void setPreneurBail(PreneurBail preneurBail) {
        this.preneurBail = preneurBail;
    }

    public AdresseDomicile getAdressePreneurBail() {
        return adressePreneurBail;
    }

    public void setAdressePreneurBail(AdresseDomicile adressePreneurBail) {
        this.adressePreneurBail = adressePreneurBail;
    }

    public Publication getPubliciteNominationLiquidateur() {
        return publiciteNominationLiquidateur;
    }

    public void setPubliciteNominationLiquidateur(Publication publiciteNominationLiquidateur) {
        this.publiciteNominationLiquidateur = publiciteNominationLiquidateur;
    }

    public MotifCessation getMotifCessation() {
        return motifCessation;
    }

    public void setMotifCessation(MotifCessation motifCessation) {
        this.motifCessation = motifCessation;
    }

    public Boolean getMaintienRcs() {
        return maintienRcs;
    }

    public void setMaintienRcs(Boolean maintienRcs) {
        this.maintienRcs = maintienRcs;
    }

    public Boolean getMaintienRm() {
        return maintienRm;
    }

    public void setMaintienRm(Boolean maintienRm) {
        this.maintienRm = maintienRm;
    }

    public LocalDate getDateCessationActiviteSalariee() {
        return dateCessationActiviteSalariee;
    }

    public void setDateCessationActiviteSalariee(LocalDate dateCessationActiviteSalariee) {
        this.dateCessationActiviteSalariee = dateCessationActiviteSalariee;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public LieuDeLiquidation getLieuDeLiquidation() {
        return lieuDeLiquidation;
    }

    public void setLieuDeLiquidation(LieuDeLiquidation lieuDeLiquidation) {
        this.lieuDeLiquidation = lieuDeLiquidation;
    }

    public LocalDate getDateCessationTotaleActivite() {
        return dateCessationTotaleActivite;
    }

    public void setDateCessationTotaleActivite(LocalDate dateCessationTotaleActivite) {
        this.dateCessationTotaleActivite = dateCessationTotaleActivite;
    }

    public LocalDate getDateClotureLiquidation() {
        return dateClotureLiquidation;
    }

    public void setDateClotureLiquidation(LocalDate dateClotureLiquidation) {
        this.dateClotureLiquidation = dateClotureLiquidation;
    }

    public LocalDate getDateTransfertPatrimoine() {
        return dateTransfertPatrimoine;
    }

    public void setDateTransfertPatrimoine(LocalDate dateTransfertPatrimoine) {
        this.dateTransfertPatrimoine = dateTransfertPatrimoine;
    }

    public LocalDate getDateDissolutionDisparition() {
        return dateDissolutionDisparition;
    }

    public void setDateDissolutionDisparition(LocalDate dateDissolutionDisparition) {
        this.dateDissolutionDisparition = dateDissolutionDisparition;
    }

    public Boolean getIndicateurCessationTemporaire() {
        return indicateurCessationTemporaire;
    }

    public void setIndicateurCessationTemporaire(Boolean indicateurCessationTemporaire) {
        this.indicateurCessationTemporaire = indicateurCessationTemporaire;
    }

    public Boolean getIndicateurDecesEntrepreneur() {
        return indicateurDecesEntrepreneur;
    }

    public void setIndicateurDecesEntrepreneur(Boolean indicateurDecesEntrepreneur) {
        this.indicateurDecesEntrepreneur = indicateurDecesEntrepreneur;
    }

    public Boolean getIndicateurPoursuiteActivite() {
        return indicateurPoursuiteActivite;
    }

    public void setIndicateurPoursuiteActivite(Boolean indicateurPoursuiteActivite) {
        this.indicateurPoursuiteActivite = indicateurPoursuiteActivite;
    }

    public Boolean getIndicateurMaintienImmatriculationRegistre() {
        return indicateurMaintienImmatriculationRegistre;
    }

    public void setIndicateurMaintienImmatriculationRegistre(Boolean indicateurMaintienImmatriculationRegistre) {
        this.indicateurMaintienImmatriculationRegistre = indicateurMaintienImmatriculationRegistre;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Boolean getIndicateurDissolution() {
        return indicateurDissolution;
    }

    public void setIndicateurDissolution(Boolean indicateurDissolution) {
        this.indicateurDissolution = indicateurDissolution;
    }

    public TypeDissolution getTypeDissolution() {
        return typeDissolution;
    }

    public void setTypeDissolution(TypeDissolution typeDissolution) {
        this.typeDissolution = typeDissolution;
    }

    public Boolean getIndicateurDisparitionPM() {
        return indicateurDisparitionPM;
    }

    public void setIndicateurDisparitionPM(Boolean indicateurDisparitionPM) {
        this.indicateurDisparitionPM = indicateurDisparitionPM;
    }

    public MotifDisparition getMotifDisparition() {
        return motifDisparition;
    }

    public void setMotifDisparition(MotifDisparition motifDisparition) {
        this.motifDisparition = motifDisparition;
    }

    public Boolean getIndicateurPresenceSalarie() {
        return indicateurPresenceSalarie;
    }

    public void setIndicateurPresenceSalarie(Boolean indicateurPresenceSalarie) {
        this.indicateurPresenceSalarie = indicateurPresenceSalarie;
    }

    public LocalDate getDateMiseEnSommeil() {
        return dateMiseEnSommeil;
    }

    public void setDateMiseEnSommeil(LocalDate dateMiseEnSommeil) {
        this.dateMiseEnSommeil = dateMiseEnSommeil;
    }

    public void setRepreneurs(List<Pouvoir> repreneurs) {
        this.repreneurs = repreneurs;
    }

}
