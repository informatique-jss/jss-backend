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
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CapaciteEngagement;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Perimetre;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SecondRoleEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourLaFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDePersonne;

@Entity
@Table(indexes = {
        @Index(name = "idx_pouvoir_composition", columnList = "id_composition"),
        @Index(name = "idx_pouvoir_detail_cessation", columnList = "id_detail_cessation_entreprise"),
        @Index(name = "idx_pouvoir_detail_etablissement", columnList = "id_detail_cessation_etablissement") })
public class Pouvoir implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_composition")
    @JsonIgnoreProperties(value = { "pouvoirs" }, allowSetters = true)
    Composition composition;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detail_cessation_entreprise")
    @JsonIgnoreProperties(value = { "repreneurs" }, allowSetters = true)
    DetailCessationEntreprise detailCessationEntreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detail_cessation_etablissement")
    @JsonIgnoreProperties(value = { "repreneurs" }, allowSetters = true)
    DetailCessationEtablissement detailCessationEtablissement;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_individu")
    IndividuRepresentant individu;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    Entreprise entreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contact")
    Contact contact;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_entreprise")
    AdresseDomicile adresseEntreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_representant")
    Repreneur representant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role_entreprise")
    RoleEntreprise roleEntreprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_pour_la_formalite")
    StatutPourLaFormalite statutPourLaFormalite;

    private LocalDate dateEffet;

    @OneToMany(mappedBy = "pouvoir")
    @JsonIgnoreProperties(value = { "pouvoir" }, allowSetters = true)
    List<EtablissementConcerne> etablissementConcerne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_de_personne")
    TypeDePersonne typeDePersonne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_capacite_engagement")
    CapaciteEngagement capaciteEngagement;

    private Boolean exonerationDesDettesAnterieures;

    private Integer montantsDesParticipation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perimetre")
    Perimetre perimetre;

    private Boolean beneficiaireEffectif;

    private Boolean indicateurSecondRoleEntreprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_second_role_entreprise")
    SecondRoleEntreprise secondRoleEntreprise;

    private Boolean isSAOrSASMajorityManager;

    @Column(length = 255)
    private String autreRoleEntreprise;

    private Boolean is31PTriggered;

    private LocalDate dateEffet31P;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public IndividuRepresentant getIndividu() {
        return individu;
    }

    public void setIndividu(IndividuRepresentant individu) {
        this.individu = individu;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public AdresseDomicile getAdresseEntreprise() {
        return adresseEntreprise;
    }

    public void setAdresseEntreprise(AdresseDomicile adresseEntreprise) {
        this.adresseEntreprise = adresseEntreprise;
    }

    public Repreneur getRepresentant() {
        return representant;
    }

    public void setRepresentant(Repreneur representant) {
        this.representant = representant;
    }

    public RoleEntreprise getRoleEntreprise() {
        return roleEntreprise;
    }

    public void setRoleEntreprise(RoleEntreprise roleEntreprise) {
        this.roleEntreprise = roleEntreprise;
    }

    public StatutPourLaFormalite getStatutPourLaFormalite() {
        return statutPourLaFormalite;
    }

    public void setStatutPourLaFormalite(StatutPourLaFormalite statutPourLaFormalite) {
        this.statutPourLaFormalite = statutPourLaFormalite;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public TypeDePersonne getTypeDePersonne() {
        return typeDePersonne;
    }

    public void setTypeDePersonne(TypeDePersonne typeDePersonne) {
        this.typeDePersonne = typeDePersonne;
    }

    public CapaciteEngagement getCapaciteEngagement() {
        return capaciteEngagement;
    }

    public void setCapaciteEngagement(CapaciteEngagement capaciteEngagement) {
        this.capaciteEngagement = capaciteEngagement;
    }

    public Boolean getExonerationDesDettesAnterieures() {
        return exonerationDesDettesAnterieures;
    }

    public void setExonerationDesDettesAnterieures(Boolean exonerationDesDettesAnterieures) {
        this.exonerationDesDettesAnterieures = exonerationDesDettesAnterieures;
    }

    public Integer getMontantsDesParticipation() {
        return montantsDesParticipation;
    }

    public void setMontantsDesParticipation(Integer montantsDesParticipation) {
        this.montantsDesParticipation = montantsDesParticipation;
    }

    public Perimetre getPerimetre() {
        return perimetre;
    }

    public void setPerimetre(Perimetre perimetre) {
        this.perimetre = perimetre;
    }

    public Boolean getBeneficiaireEffectif() {
        return beneficiaireEffectif;
    }

    public void setBeneficiaireEffectif(Boolean beneficiaireEffectif) {
        this.beneficiaireEffectif = beneficiaireEffectif;
    }

    public Boolean getIndicateurSecondRoleEntreprise() {
        return indicateurSecondRoleEntreprise;
    }

    public void setIndicateurSecondRoleEntreprise(Boolean indicateurSecondRoleEntreprise) {
        this.indicateurSecondRoleEntreprise = indicateurSecondRoleEntreprise;
    }

    public SecondRoleEntreprise getSecondRoleEntreprise() {
        return secondRoleEntreprise;
    }

    public void setSecondRoleEntreprise(SecondRoleEntreprise secondRoleEntreprise) {
        this.secondRoleEntreprise = secondRoleEntreprise;
    }

    public Boolean getIsSAOrSASMajorityManager() {
        return isSAOrSASMajorityManager;
    }

    public void setIsSAOrSASMajorityManager(Boolean isSAOrSASMajorityManager) {
        this.isSAOrSASMajorityManager = isSAOrSASMajorityManager;
    }

    public String getAutreRoleEntreprise() {
        return autreRoleEntreprise;
    }

    public void setAutreRoleEntreprise(String autreRoleEntreprise) {
        this.autreRoleEntreprise = autreRoleEntreprise;
    }

    public Boolean getIs31PTriggered() {
        return is31PTriggered;
    }

    public void setIs31PTriggered(Boolean is31pTriggered) {
        is31PTriggered = is31pTriggered;
    }

    public LocalDate getDateEffet31P() {
        return dateEffet31P;
    }

    public void setDateEffet31P(LocalDate dateEffet31P) {
        this.dateEffet31P = dateEffet31P;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public DetailCessationEntreprise getDetailCessationEntreprise() {
        return detailCessationEntreprise;
    }

    public void setDetailCessationEntreprise(DetailCessationEntreprise detailCessationEntreprise) {
        this.detailCessationEntreprise = detailCessationEntreprise;
    }

    public DetailCessationEtablissement getDetailCessationEtablissement() {
        return detailCessationEtablissement;
    }

    public void setDetailCessationEtablissement(DetailCessationEtablissement detailCessationEtablissement) {
        this.detailCessationEtablissement = detailCessationEtablissement;
    }

    public List<EtablissementConcerne> getEtablissementConcerne() {
        return etablissementConcerne;
    }

    public void setEtablissementConcerne(List<EtablissementConcerne> etablissementConcerne) {
        this.etablissementConcerne = etablissementConcerne;
    }

}
