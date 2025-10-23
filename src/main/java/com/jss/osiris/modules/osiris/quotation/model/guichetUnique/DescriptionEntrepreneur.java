package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.CodeNationalite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeSociale;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.Genre;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.QualiteDeNonSedentarite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.Role;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.SituationMatrimoniale;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutVisAVisFormalite;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DescriptionEntrepreneur implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @Column(length = 255)
    private String siren;

    @Column(length = 255)
    private String nicSiege;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    Role role;

    private LocalDate dateEffetRoleDeclarant;

    @Column(length = 255)
    private String numeroSecu;

    @Column(length = 255)
    private String nom;

    @OneToMany(mappedBy = "descriptionEntrepreneur")
    @JsonIgnoreProperties(value = { "descriptionEntrepreneur" }, allowSetters = true)
    List<Prenom> prenoms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genre")
    Genre genre;

    @Column(length = 255)
    private String titre;

    @Column(length = 255)
    private String nomUsage;

    @Column(length = 255)
    private String pseudonyme;

    private String dateDeNaissance;

    @Column(length = 255)
    private String paysNaissance;

    @Column(length = 255)
    private String lieuDeNaissance;

    @Column(length = 255)
    private String codePostalNaissance;

    @Column(length = 255)
    private String codeInseeGeographique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_code_nationalite")
    CodeNationalite codeNationalite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situation_matrimoniale")
    SituationMatrimoniale situationMatrimoniale;

    @Column(length = 255)
    private String optionRgpd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qualite_de_non_sedentarite")
    QualiteDeNonSedentarite qualiteDeNonSedentarite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_vis_a_vis_formalite")
    StatutVisAVisFormalite statutVisAVisFormalite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forme_sociale")
    FormeSociale formeSociale;

    private Boolean indicateurDeNonSedentarite;

    private Boolean confirmRnippMismatch;

    private LocalDate dateEffet15P;

    private LocalDate dateEffet10P;

    private LocalDate dateEffet17P;

    private Boolean is10PTriggered;

    private Boolean is15PTriggered;

    private Boolean is17PTriggered;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getNicSiege() {
        return nicSiege;
    }

    public void setNicSiege(String nicSiege) {
        this.nicSiege = nicSiege;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getDateEffetRoleDeclarant() {
        return dateEffetRoleDeclarant;
    }

    public void setDateEffetRoleDeclarant(LocalDate dateEffetRoleDeclarant) {
        this.dateEffetRoleDeclarant = dateEffetRoleDeclarant;
    }

    public String getNumeroSecu() {
        return numeroSecu;
    }

    public void setNumeroSecu(String numeroSecu) {
        this.numeroSecu = numeroSecu;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getNomUsage() {
        return nomUsage;
    }

    public void setNomUsage(String nomUsage) {
        this.nomUsage = nomUsage;
    }

    public String getPseudonyme() {
        return pseudonyme;
    }

    public void setPseudonyme(String pseudonyme) {
        this.pseudonyme = pseudonyme;
    }

    public String getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(String dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getPaysNaissance() {
        return paysNaissance;
    }

    public void setPaysNaissance(String paysNaissance) {
        this.paysNaissance = paysNaissance;
    }

    public String getLieuDeNaissance() {
        return lieuDeNaissance;
    }

    public void setLieuDeNaissance(String lieuDeNaissance) {
        this.lieuDeNaissance = lieuDeNaissance;
    }

    public String getCodePostalNaissance() {
        return codePostalNaissance;
    }

    public void setCodePostalNaissance(String codePostalNaissance) {
        this.codePostalNaissance = codePostalNaissance;
    }

    public String getCodeInseeGeographique() {
        return codeInseeGeographique;
    }

    public void setCodeInseeGeographique(String codeInseeGeographique) {
        this.codeInseeGeographique = codeInseeGeographique;
    }

    public CodeNationalite getCodeNationalite() {
        return codeNationalite;
    }

    public void setCodeNationalite(CodeNationalite codeNationalite) {
        this.codeNationalite = codeNationalite;
    }

    public SituationMatrimoniale getSituationMatrimoniale() {
        return situationMatrimoniale;
    }

    public void setSituationMatrimoniale(SituationMatrimoniale situationMatrimoniale) {
        this.situationMatrimoniale = situationMatrimoniale;
    }

    public String getOptionRgpd() {
        return optionRgpd;
    }

    public void setOptionRgpd(String optionRgpd) {
        this.optionRgpd = optionRgpd;
    }

    public QualiteDeNonSedentarite getQualiteDeNonSedentarite() {
        return qualiteDeNonSedentarite;
    }

    public void setQualiteDeNonSedentarite(QualiteDeNonSedentarite qualiteDeNonSedentarite) {
        this.qualiteDeNonSedentarite = qualiteDeNonSedentarite;
    }

    public StatutVisAVisFormalite getStatutVisAVisFormalite() {
        return statutVisAVisFormalite;
    }

    public void setStatutVisAVisFormalite(StatutVisAVisFormalite statutVisAVisFormalite) {
        this.statutVisAVisFormalite = statutVisAVisFormalite;
    }

    public FormeSociale getFormeSociale() {
        return formeSociale;
    }

    public void setFormeSociale(FormeSociale formeSociale) {
        this.formeSociale = formeSociale;
    }

    public Boolean getIndicateurDeNonSedentarite() {
        return indicateurDeNonSedentarite;
    }

    public void setIndicateurDeNonSedentarite(Boolean indicateurDeNonSedentarite) {
        this.indicateurDeNonSedentarite = indicateurDeNonSedentarite;
    }

    public Boolean getConfirmRnippMismatch() {
        return confirmRnippMismatch;
    }

    public void setConfirmRnippMismatch(Boolean confirmRnippMismatch) {
        this.confirmRnippMismatch = confirmRnippMismatch;
    }

    public LocalDate getDateEffet15P() {
        return dateEffet15P;
    }

    public void setDateEffet15P(LocalDate dateEffet15P) {
        this.dateEffet15P = dateEffet15P;
    }

    public LocalDate getDateEffet10P() {
        return dateEffet10P;
    }

    public void setDateEffet10P(LocalDate dateEffet10P) {
        this.dateEffet10P = dateEffet10P;
    }

    public LocalDate getDateEffet17P() {
        return dateEffet17P;
    }

    public void setDateEffet17P(LocalDate dateEffet17P) {
        this.dateEffet17P = dateEffet17P;
    }

    public Boolean getIs10PTriggered() {
        return is10PTriggered;
    }

    public void setIs10PTriggered(Boolean is10pTriggered) {
        is10PTriggered = is10pTriggered;
    }

    public Boolean getIs15PTriggered() {
        return is15PTriggered;
    }

    public void setIs15PTriggered(Boolean is15pTriggered) {
        is15PTriggered = is15pTriggered;
    }

    public Boolean getIs17PTriggered() {
        return is17PTriggered;
    }

    public void setIs17PTriggered(Boolean is17pTriggered) {
        is17PTriggered = is17pTriggered;
    }

    public List<Prenom> getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(List<Prenom> prenoms) {
        this.prenoms = prenoms;
    }

}
