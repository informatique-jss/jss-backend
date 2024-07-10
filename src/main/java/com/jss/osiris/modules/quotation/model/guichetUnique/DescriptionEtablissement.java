package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DestinationEtablissement;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RolePourEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourFormalite;

@Entity
@DoNotAudit
public class DescriptionEtablissement implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role_pour_entreprise")
    RolePourEntreprise rolePourEntreprise;

    private Boolean indicateurEtranger;

    @Column(length = 255)
    private String pays;

    @Column(length = 255)
    private String siret;

    @Column(length = 255)
    private String autreIdentifiantEtranger;

    private Boolean indicateurDomiciliataire;

    @Column(length = 255)
    private String identifiantTemporaire;

    private Boolean activiteNonSedentaire;

    @Column(length = 255)
    private String enseigne;

    @Column(length = 2500)
    private String nomCommercial;

    private Boolean autonomieJuridique;

    @Column(length = 255)
    private String greffeImmatriculation;

    @Column(length = 255)
    private String lieuImmatriculation;

    private LocalDate dateFinToutEffectifSalarie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destination_etablissement")
    DestinationEtablissement destinationEtablissement;

    @Column(length = 255)
    private String autreDestination;

    private Boolean sansActiviteAutreActiviteSiege;

    private Boolean indicateurEtablissementPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_pour_formalite")
    StatutPourFormalite statutPourFormalite;

    private LocalDate dateEffet;

    private LocalDate dateEffetFermeture;

    private LocalDate dateEffetTransfert;

    @Column(length = 255)
    private String nomEtablissement;

    @Column(length = 255)
    private String statutOuvertFerme;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RolePourEntreprise getRolePourEntreprise() {
        return rolePourEntreprise;
    }

    public void setRolePourEntreprise(RolePourEntreprise rolePourEntreprise) {
        this.rolePourEntreprise = rolePourEntreprise;
    }

    public Boolean getIndicateurEtranger() {
        return indicateurEtranger;
    }

    public void setIndicateurEtranger(Boolean indicateurEtranger) {
        this.indicateurEtranger = indicateurEtranger;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getAutreIdentifiantEtranger() {
        return autreIdentifiantEtranger;
    }

    public void setAutreIdentifiantEtranger(String autreIdentifiantEtranger) {
        this.autreIdentifiantEtranger = autreIdentifiantEtranger;
    }

    public Boolean getIndicateurDomiciliataire() {
        return indicateurDomiciliataire;
    }

    public void setIndicateurDomiciliataire(Boolean indicateurDomiciliataire) {
        this.indicateurDomiciliataire = indicateurDomiciliataire;
    }

    public String getIdentifiantTemporaire() {
        return identifiantTemporaire;
    }

    public void setIdentifiantTemporaire(String identifiantTemporaire) {
        this.identifiantTemporaire = identifiantTemporaire;
    }

    public Boolean getActiviteNonSedentaire() {
        return activiteNonSedentaire;
    }

    public void setActiviteNonSedentaire(Boolean activiteNonSedentaire) {
        this.activiteNonSedentaire = activiteNonSedentaire;
    }

    public String getEnseigne() {
        return enseigne;
    }

    public void setEnseigne(String enseigne) {
        this.enseigne = enseigne;
    }

    public String getNomCommercial() {
        return nomCommercial;
    }

    public void setNomCommercial(String nomCommercial) {
        this.nomCommercial = nomCommercial;
    }

    public Boolean getAutonomieJuridique() {
        return autonomieJuridique;
    }

    public void setAutonomieJuridique(Boolean autonomieJuridique) {
        this.autonomieJuridique = autonomieJuridique;
    }

    public String getGreffeImmatriculation() {
        return greffeImmatriculation;
    }

    public void setGreffeImmatriculation(String greffeImmatriculation) {
        this.greffeImmatriculation = greffeImmatriculation;
    }

    public String getLieuImmatriculation() {
        return lieuImmatriculation;
    }

    public void setLieuImmatriculation(String lieuImmatriculation) {
        this.lieuImmatriculation = lieuImmatriculation;
    }

    public LocalDate getDateFinToutEffectifSalarie() {
        return dateFinToutEffectifSalarie;
    }

    public void setDateFinToutEffectifSalarie(LocalDate dateFinToutEffectifSalarie) {
        this.dateFinToutEffectifSalarie = dateFinToutEffectifSalarie;
    }

    public DestinationEtablissement getDestinationEtablissement() {
        return destinationEtablissement;
    }

    public void setDestinationEtablissement(DestinationEtablissement destinationEtablissement) {
        this.destinationEtablissement = destinationEtablissement;
    }

    public String getAutreDestination() {
        return autreDestination;
    }

    public void setAutreDestination(String autreDestination) {
        this.autreDestination = autreDestination;
    }

    public Boolean getSansActiviteAutreActiviteSiege() {
        return sansActiviteAutreActiviteSiege;
    }

    public void setSansActiviteAutreActiviteSiege(Boolean sansActiviteAutreActiviteSiege) {
        this.sansActiviteAutreActiviteSiege = sansActiviteAutreActiviteSiege;
    }

    public Boolean getIndicateurEtablissementPrincipal() {
        return indicateurEtablissementPrincipal;
    }

    public void setIndicateurEtablissementPrincipal(Boolean indicateurEtablissementPrincipal) {
        this.indicateurEtablissementPrincipal = indicateurEtablissementPrincipal;
    }

    public StatutPourFormalite getStatutPourFormalite() {
        return statutPourFormalite;
    }

    public void setStatutPourFormalite(StatutPourFormalite statutPourFormalite) {
        this.statutPourFormalite = statutPourFormalite;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public LocalDate getDateEffetFermeture() {
        return dateEffetFermeture;
    }

    public void setDateEffetFermeture(LocalDate dateEffetFermeture) {
        this.dateEffetFermeture = dateEffetFermeture;
    }

    public LocalDate getDateEffetTransfert() {
        return dateEffetTransfert;
    }

    public void setDateEffetTransfert(LocalDate dateEffetTransfert) {
        this.dateEffetTransfert = dateEffetTransfert;
    }

    public String getNomEtablissement() {
        return nomEtablissement;
    }

    public void setNomEtablissement(String nomEtablissement) {
        this.nomEtablissement = nomEtablissement;
    }

    public String getStatutOuvertFerme() {
        return statutOuvertFerme;
    }

    public void setStatutOuvertFerme(String statutOuvertFerme) {
        this.statutOuvertFerme = statutOuvertFerme;
    }

}
