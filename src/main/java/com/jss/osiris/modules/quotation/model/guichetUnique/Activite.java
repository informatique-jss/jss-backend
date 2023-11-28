package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ActiviteReguliere;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ExerciceActivite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeExercice;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PrecisionActivite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.QualiteNonSedentaire;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPraticien;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TotalitePartie;

@Entity
@Table(indexes = {
        @Index(name = "idx_activite_autre_etablissement", columnList = "id_autres_etablissement"),
        @Index(name = "idx_activite_etablissement_principal", columnList = "id_etablissement_principal") })
public class Activite implements Serializable, IId {

    public Activite(String activite) {
        this.precisionAutre = activite;
    }

    public Activite() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etablissement_principal")
    @JsonIgnoreProperties(value = { "activites" }, allowSetters = true)
    EtablissementPrincipal etablissementPrincipal;

    @Column(length = 255)
    private String rolePourEtablissement;

    private LocalDate dateEffet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_formalite")
    StatutFormalite statutFormalite;

    private Boolean indicateurPrincipal;

    private Boolean indicateurProlongement;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exercice_activite")
    ExerciceActivite exerciceActivite;

    private LocalDate dateDebutPeriode;

    private LocalDate dateFinPeriode;

    private Boolean indicateurNonSedentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forme_exercice")
    FormeExercice formeExercice;

    @Column(length = 255)
    private String categorisationActivite1;

    @Column(length = 255)
    private String precisionAutreCategorie1;

    @Column(length = 255)
    private String categorisationActivite2;

    @Column(length = 255)
    private String precisionAutreCategorie2;

    @Column(length = 255)
    private String categorisationActivite3;

    @Column(length = 255)
    private String precisionAutreCategorie3;

    @Column(length = 255)
    private String categorisationActivite4;

    @Column(length = 255)
    private String precisionAutreCategorie4;

    @Column(length = 255)
    private String codeAprm;

    @Column(length = 4000)
    private String descriptionDetaillee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_precision_activite")
    PrecisionActivite precisionActivite;

    @Column(length = 255)
    private String precisionAutre;

    private Integer surface;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qualite_non_sedentaire")
    QualiteNonSedentaire qualiteNonSedentaire;

    @Column(length = 255)
    private String autreMotifModification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_totalite_partie")
    TotalitePartie totalitePartie;

    private Boolean locationDpb;

    private Boolean indicateurArtisteAuteur;

    private Boolean soumissionAuPrecompte;

    private Boolean indicateurMarinProfessionnel;

    private Boolean rolePrincipalPourEntreprise;

    @Column(length = 255)
    private String codeApe;

    @Column(length = 255)
    private String numPraticien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_praticien")
    StatutPraticien statutPraticien;

    private Boolean activiteRattacheeEirl;

    @Column(length = 255)
    private String denominationEirlRattachee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_activite_reguliere")
    ActiviteReguliere activiteReguliere;

    private Boolean indicateurPremiereActivite;

    private LocalDate dateEffetTransfert;

    @Column(length = 255)
    private String identifiantTemporaireEtablissementDestination;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ancienne_adresse")
    AdresseDomicile ancienneAdresse;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_origine")
    Origine origine;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jqpa")
    Jqpa jqpa;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_location_gerance_mandat")
    LocationGeranceMandat locationGeranceMandat;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_locataire_gerant_mandataire")
    LocataireGerantMandataire locataireGerantMandataire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_gerant_mandataire")
    AdresseDomicile adresseGerantMandataire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "activites" }, allowSetters = true)
    @JoinColumn(name = "id_autres_etablissement")
    AutresEtablissement autresEtablissement;

    private Boolean is20PTriggered;

    private Boolean is61PMFTriggered;

    private Boolean is62PMTriggered;

    private Boolean is67PMTriggered;

    private LocalDate dateEffet20P;

    private LocalDate dateEffetAjoutActivite;

    private LocalDate dateEffet67PM;

    private Boolean is24Or27PMTriggered;

    private LocalDate dateEffet24Or27PM;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_effectif_salarie")
    EffectifSalarie effectifSalarie;

    @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "activite" }, allowSetters = true)
    List<Building> buildings;

    private Boolean is20MTriggered;

    private LocalDate dateEffet20M;

    @Column(length = 2000)
    private String destinationActivite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRolePourEtablissement() {
        return rolePourEtablissement;
    }

    public void setRolePourEtablissement(String rolePourEtablissement) {
        this.rolePourEtablissement = rolePourEtablissement;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public StatutFormalite getStatutFormalite() {
        return statutFormalite;
    }

    public void setStatutFormalite(StatutFormalite statutFormalite) {
        this.statutFormalite = statutFormalite;
    }

    public Boolean getIndicateurPrincipal() {
        return indicateurPrincipal;
    }

    public void setIndicateurPrincipal(Boolean indicateurPrincipal) {
        this.indicateurPrincipal = indicateurPrincipal;
    }

    public Boolean getIndicateurProlongement() {
        return indicateurProlongement;
    }

    public void setIndicateurProlongement(Boolean indicateurProlongement) {
        this.indicateurProlongement = indicateurProlongement;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public ExerciceActivite getExerciceActivite() {
        return exerciceActivite;
    }

    public void setExerciceActivite(ExerciceActivite exerciceActivite) {
        this.exerciceActivite = exerciceActivite;
    }

    public LocalDate getDateDebutPeriode() {
        return dateDebutPeriode;
    }

    public void setDateDebutPeriode(LocalDate dateDebutPeriode) {
        this.dateDebutPeriode = dateDebutPeriode;
    }

    public LocalDate getDateFinPeriode() {
        return dateFinPeriode;
    }

    public void setDateFinPeriode(LocalDate dateFinPeriode) {
        this.dateFinPeriode = dateFinPeriode;
    }

    public Boolean getIndicateurNonSedentaire() {
        return indicateurNonSedentaire;
    }

    public void setIndicateurNonSedentaire(Boolean indicateurNonSedentaire) {
        this.indicateurNonSedentaire = indicateurNonSedentaire;
    }

    public FormeExercice getFormeExercice() {
        return formeExercice;
    }

    public void setFormeExercice(FormeExercice formeExercice) {
        this.formeExercice = formeExercice;
    }

    public String getCategorisationActivite1() {
        return categorisationActivite1;
    }

    public void setCategorisationActivite1(String categorisationActivite1) {
        this.categorisationActivite1 = categorisationActivite1;
    }

    public String getPrecisionAutreCategorie1() {
        return precisionAutreCategorie1;
    }

    public void setPrecisionAutreCategorie1(String precisionAutreCategorie1) {
        this.precisionAutreCategorie1 = precisionAutreCategorie1;
    }

    public String getCategorisationActivite2() {
        return categorisationActivite2;
    }

    public void setCategorisationActivite2(String categorisationActivite2) {
        this.categorisationActivite2 = categorisationActivite2;
    }

    public String getPrecisionAutreCategorie2() {
        return precisionAutreCategorie2;
    }

    public void setPrecisionAutreCategorie2(String precisionAutreCategorie2) {
        this.precisionAutreCategorie2 = precisionAutreCategorie2;
    }

    public String getCategorisationActivite3() {
        return categorisationActivite3;
    }

    public void setCategorisationActivite3(String categorisationActivite3) {
        this.categorisationActivite3 = categorisationActivite3;
    }

    public String getPrecisionAutreCategorie3() {
        return precisionAutreCategorie3;
    }

    public void setPrecisionAutreCategorie3(String precisionAutreCategorie3) {
        this.precisionAutreCategorie3 = precisionAutreCategorie3;
    }

    public String getCategorisationActivite4() {
        return categorisationActivite4;
    }

    public void setCategorisationActivite4(String categorisationActivite4) {
        this.categorisationActivite4 = categorisationActivite4;
    }

    public String getPrecisionAutreCategorie4() {
        return precisionAutreCategorie4;
    }

    public void setPrecisionAutreCategorie4(String precisionAutreCategorie4) {
        this.precisionAutreCategorie4 = precisionAutreCategorie4;
    }

    public String getCodeAprm() {
        return codeAprm;
    }

    public void setCodeAprm(String codeAprm) {
        this.codeAprm = codeAprm;
    }

    public String getDescriptionDetaillee() {
        return descriptionDetaillee;
    }

    public void setDescriptionDetaillee(String descriptionDetaillee) {
        this.descriptionDetaillee = descriptionDetaillee;
    }

    public PrecisionActivite getPrecisionActivite() {
        return precisionActivite;
    }

    public void setPrecisionActivite(PrecisionActivite precisionActivite) {
        this.precisionActivite = precisionActivite;
    }

    public String getPrecisionAutre() {
        return precisionAutre;
    }

    public void setPrecisionAutre(String precisionAutre) {
        this.precisionAutre = precisionAutre;
    }

    public Integer getSurface() {
        return surface;
    }

    public void setSurface(Integer surface) {
        this.surface = surface;
    }

    public QualiteNonSedentaire getQualiteNonSedentaire() {
        return qualiteNonSedentaire;
    }

    public void setQualiteNonSedentaire(QualiteNonSedentaire qualiteNonSedentaire) {
        this.qualiteNonSedentaire = qualiteNonSedentaire;
    }

    public String getAutreMotifModification() {
        return autreMotifModification;
    }

    public void setAutreMotifModification(String autreMotifModification) {
        this.autreMotifModification = autreMotifModification;
    }

    public TotalitePartie getTotalitePartie() {
        return totalitePartie;
    }

    public void setTotalitePartie(TotalitePartie totalitePartie) {
        this.totalitePartie = totalitePartie;
    }

    public Boolean getLocationDpb() {
        return locationDpb;
    }

    public void setLocationDpb(Boolean locationDpb) {
        this.locationDpb = locationDpb;
    }

    public Boolean getIndicateurArtisteAuteur() {
        return indicateurArtisteAuteur;
    }

    public void setIndicateurArtisteAuteur(Boolean indicateurArtisteAuteur) {
        this.indicateurArtisteAuteur = indicateurArtisteAuteur;
    }

    public Boolean getSoumissionAuPrecompte() {
        return soumissionAuPrecompte;
    }

    public void setSoumissionAuPrecompte(Boolean soumissionAuPrecompte) {
        this.soumissionAuPrecompte = soumissionAuPrecompte;
    }

    public Boolean getIndicateurMarinProfessionnel() {
        return indicateurMarinProfessionnel;
    }

    public void setIndicateurMarinProfessionnel(Boolean indicateurMarinProfessionnel) {
        this.indicateurMarinProfessionnel = indicateurMarinProfessionnel;
    }

    public Boolean getRolePrincipalPourEntreprise() {
        return rolePrincipalPourEntreprise;
    }

    public void setRolePrincipalPourEntreprise(Boolean rolePrincipalPourEntreprise) {
        this.rolePrincipalPourEntreprise = rolePrincipalPourEntreprise;
    }

    public String getCodeApe() {
        return codeApe;
    }

    public void setCodeApe(String codeApe) {
        this.codeApe = codeApe;
    }

    public String getNumPraticien() {
        return numPraticien;
    }

    public void setNumPraticien(String numPraticien) {
        this.numPraticien = numPraticien;
    }

    public StatutPraticien getStatutPraticien() {
        return statutPraticien;
    }

    public void setStatutPraticien(StatutPraticien statutPraticien) {
        this.statutPraticien = statutPraticien;
    }

    public Boolean getActiviteRattacheeEirl() {
        return activiteRattacheeEirl;
    }

    public void setActiviteRattacheeEirl(Boolean activiteRattacheeEirl) {
        this.activiteRattacheeEirl = activiteRattacheeEirl;
    }

    public String getDenominationEirlRattachee() {
        return denominationEirlRattachee;
    }

    public void setDenominationEirlRattachee(String denominationEirlRattachee) {
        this.denominationEirlRattachee = denominationEirlRattachee;
    }

    public ActiviteReguliere getActiviteReguliere() {
        return activiteReguliere;
    }

    public void setActiviteReguliere(ActiviteReguliere activiteReguliere) {
        this.activiteReguliere = activiteReguliere;
    }

    public Boolean getIndicateurPremiereActivite() {
        return indicateurPremiereActivite;
    }

    public void setIndicateurPremiereActivite(Boolean indicateurPremiereActivite) {
        this.indicateurPremiereActivite = indicateurPremiereActivite;
    }

    public LocalDate getDateEffetTransfert() {
        return dateEffetTransfert;
    }

    public void setDateEffetTransfert(LocalDate dateEffetTransfert) {
        this.dateEffetTransfert = dateEffetTransfert;
    }

    public String getIdentifiantTemporaireEtablissementDestination() {
        return identifiantTemporaireEtablissementDestination;
    }

    public void setIdentifiantTemporaireEtablissementDestination(String identifiantTemporaireEtablissementDestination) {
        this.identifiantTemporaireEtablissementDestination = identifiantTemporaireEtablissementDestination;
    }

    public AdresseDomicile getAncienneAdresse() {
        return ancienneAdresse;
    }

    public void setAncienneAdresse(AdresseDomicile ancienneAdresse) {
        this.ancienneAdresse = ancienneAdresse;
    }

    public Origine getOrigine() {
        return origine;
    }

    public void setOrigine(Origine origine) {
        this.origine = origine;
    }

    public Jqpa getJqpa() {
        return jqpa;
    }

    public void setJqpa(Jqpa jqpa) {
        this.jqpa = jqpa;
    }

    public LocationGeranceMandat getLocationGeranceMandat() {
        return locationGeranceMandat;
    }

    public void setLocationGeranceMandat(LocationGeranceMandat locationGeranceMandat) {
        this.locationGeranceMandat = locationGeranceMandat;
    }

    public LocataireGerantMandataire getLocataireGerantMandataire() {
        return locataireGerantMandataire;
    }

    public void setLocataireGerantMandataire(LocataireGerantMandataire locataireGerantMandataire) {
        this.locataireGerantMandataire = locataireGerantMandataire;
    }

    public AdresseDomicile getAdresseGerantMandataire() {
        return adresseGerantMandataire;
    }

    public void setAdresseGerantMandataire(AdresseDomicile adresseGerantMandataire) {
        this.adresseGerantMandataire = adresseGerantMandataire;
    }

    public Boolean getIs20PTriggered() {
        return is20PTriggered;
    }

    public void setIs20PTriggered(Boolean is20pTriggered) {
        is20PTriggered = is20pTriggered;
    }

    public Boolean getIs61PMFTriggered() {
        return is61PMFTriggered;
    }

    public void setIs61PMFTriggered(Boolean is61pmfTriggered) {
        is61PMFTriggered = is61pmfTriggered;
    }

    public Boolean getIs62PMTriggered() {
        return is62PMTriggered;
    }

    public void setIs62PMTriggered(Boolean is62pmTriggered) {
        is62PMTriggered = is62pmTriggered;
    }

    public Boolean getIs67PMTriggered() {
        return is67PMTriggered;
    }

    public void setIs67PMTriggered(Boolean is67pmTriggered) {
        is67PMTriggered = is67pmTriggered;
    }

    public LocalDate getDateEffet20P() {
        return dateEffet20P;
    }

    public void setDateEffet20P(LocalDate dateEffet20P) {
        this.dateEffet20P = dateEffet20P;
    }

    public LocalDate getDateEffetAjoutActivite() {
        return dateEffetAjoutActivite;
    }

    public void setDateEffetAjoutActivite(LocalDate dateEffetAjoutActivite) {
        this.dateEffetAjoutActivite = dateEffetAjoutActivite;
    }

    public LocalDate getDateEffet67PM() {
        return dateEffet67PM;
    }

    public void setDateEffet67PM(LocalDate dateEffet67PM) {
        this.dateEffet67PM = dateEffet67PM;
    }

    public Boolean getIs24Or27PMTriggered() {
        return is24Or27PMTriggered;
    }

    public void setIs24Or27PMTriggered(Boolean is24Or27PMTriggered) {
        this.is24Or27PMTriggered = is24Or27PMTriggered;
    }

    public LocalDate getDateEffet24Or27PM() {
        return dateEffet24Or27PM;
    }

    public void setDateEffet24Or27PM(LocalDate dateEffet24Or27PM) {
        this.dateEffet24Or27PM = dateEffet24Or27PM;
    }

    public EffectifSalarie getEffectifSalarie() {
        return effectifSalarie;
    }

    public void setEffectifSalarie(EffectifSalarie effectifSalarie) {
        this.effectifSalarie = effectifSalarie;
    }

    public Boolean getIs20MTriggered() {
        return is20MTriggered;
    }

    public void setIs20MTriggered(Boolean is20mTriggered) {
        is20MTriggered = is20mTriggered;
    }

    public LocalDate getDateEffet20M() {
        return dateEffet20M;
    }

    public void setDateEffet20M(LocalDate dateEffet20M) {
        this.dateEffet20M = dateEffet20M;
    }

    public String getDestinationActivite() {
        return destinationActivite;
    }

    public void setDestinationActivite(String destinationActivite) {
        this.destinationActivite = destinationActivite;
    }

    public AutresEtablissement getAutresEtablissement() {
        return autresEtablissement;
    }

    public void setAutresEtablissement(AutresEtablissement autresEtablissement) {
        this.autresEtablissement = autresEtablissement;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public EtablissementPrincipal getEtablissementPrincipal() {
        return etablissementPrincipal;
    }

    public void setEtablissementPrincipal(EtablissementPrincipal etablissementPrincipal) {
        this.etablissementPrincipal = etablissementPrincipal;
    }

}
