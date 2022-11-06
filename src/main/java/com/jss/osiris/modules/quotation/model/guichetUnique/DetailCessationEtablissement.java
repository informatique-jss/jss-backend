package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Destination;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.LieuDeLiquidation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifCessation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifDisparition;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDissolution;

@Entity
public class DetailCessationEtablissement implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy = "detailCessationEtablissement")
    @JsonIgnoreProperties(value = { "detailCessationEtablissement" }, allowSetters = true)
    List<Pouvoir> repreneurs;

    @ManyToOne
    @JoinColumn(name = "id_adresse", nullable = false)
    AdresseDomicile adresse;

    @ManyToOne
    @JoinColumn(name = "id_preneur_bail", nullable = false)
    PreneurBail preneurBail;

    @ManyToOne
    @JoinColumn(name = "id_adresse_preneur_bail", nullable = false)
    AdresseDomicile adressePreneurBail;

    @ManyToOne
    @JoinColumn(name = "id_publicite_nomination_liquidateur", nullable = false)
    Publication publiciteNominationLiquidateur;

    @ManyToOne
    @JoinColumn(name = "id_motif_cessation", nullable = false)
    MotifCessation motifCessation;

    @Column(nullable = false)
    private Boolean maintienRcs;

    @Column(nullable = false)
    private Boolean maintienRm;

    @Column(nullable = false)
    private LocalDate dateCessationActiviteSalariee;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @ManyToOne
    @JoinColumn(name = "id_lieu_de_liquidation", nullable = false)
    LieuDeLiquidation lieuDeLiquidation;

    @Column(nullable = false)
    private LocalDate dateCessationTotaleActivite;

    @Column(nullable = false)
    private LocalDate dateClotureLiquidation;

    @Column(nullable = false)
    private LocalDate dateTransfertPatrimoine;

    @Column(nullable = false)
    private LocalDate dateDissolutionDisparition;

    @Column(nullable = false)
    private Boolean indicateurCessationTemporaire;

    @Column(nullable = false)
    private Boolean indicateurDecesEntrepreneur;

    @Column(nullable = false)
    private Boolean indicateurPoursuiteActivite;

    @Column(nullable = false)
    private Boolean indicateurMaintienImmatriculationRegistre;

    @ManyToOne
    @JoinColumn(name = "id_destination", nullable = false)
    Destination destination;

    @Column(nullable = false)
    private Boolean indicateurDissolution;

    @ManyToOne
    @JoinColumn(name = "id_type_dissolution", nullable = false)
    TypeDissolution typeDissolution;

    @Column(nullable = false)
    private Boolean indicateurDisparitionPM;

    @ManyToOne
    @JoinColumn(name = "id_motif_disparition", nullable = false)
    MotifDisparition motifDisparition;

    @Column(nullable = false)
    private Boolean indicateurPresenceSalarie;

    @Column(nullable = false)
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

    public List<Pouvoir> getRepreneurs() {
        return repreneurs;
    }

    public void setRepreneurs(List<Pouvoir> repreneurs) {
        this.repreneurs = repreneurs;
    }

}
