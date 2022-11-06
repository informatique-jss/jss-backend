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
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CapaciteEngagement;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Perimetre;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SecondRoleEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourLaFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDePersonne;

@Entity
public class Pouvoir implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_composition", nullable = false)
    @JsonIgnoreProperties(value = { "pouvoirs" }, allowSetters = true)
    Composition composition;

    @ManyToOne
    @JoinColumn(name = "id_detail_cessation_entreprise", nullable = false)
    @JsonIgnoreProperties(value = { "repreneurs" }, allowSetters = true)
    DetailCessationEntreprise detailCessationEntreprise;

    @ManyToOne
    @JoinColumn(name = "id_detail_cessation_etablissement", nullable = false)
    @JsonIgnoreProperties(value = { "repreneurs" }, allowSetters = true)
    DetailCessationEtablissement detailCessationEtablissement;

    @ManyToOne
    @JoinColumn(name = "id_individu", nullable = false)
    IndividuRepresentant individu;

    @ManyToOne
    @JoinColumn(name = "id_entreprise", nullable = false)
    Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "id_contact", nullable = false)
    Contact contact;

    @ManyToOne
    @JoinColumn(name = "id_adresse_entreprise", nullable = false)
    AdresseDomicile adresseEntreprise;

    @ManyToOne
    @JoinColumn(name = "id_representant", nullable = false)
    Repreneur representant;

    @ManyToOne
    @JoinColumn(name = "id_role_entreprise", nullable = false)
    RoleEntreprise roleEntreprise;

    @ManyToOne
    @JoinColumn(name = "id_statut_pour_la_formalite", nullable = false)
    StatutPourLaFormalite statutPourLaFormalite;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @OneToMany(mappedBy = "pouvoir")
    @JsonIgnoreProperties(value = { "pouvoir" }, allowSetters = true)
    List<EtablissementConcerne> etablissementConcerne;

    @ManyToOne
    @JoinColumn(name = "id_type_de_personne", nullable = false)
    TypeDePersonne typeDePersonne;

    @ManyToOne
    @JoinColumn(name = "id_capacite_engagement", nullable = false)
    CapaciteEngagement capaciteEngagement;

    @Column(nullable = false)
    private Boolean exonerationDesDettesAnterieures;

    @Column(nullable = false)
    private Integer montantsDesParticipation;

    @ManyToOne
    @JoinColumn(name = "id_perimetre", nullable = false)
    Perimetre perimetre;

    @Column(nullable = false)
    private Boolean beneficiaireEffectif;

    @Column(nullable = false)
    private Boolean indicateurSecondRoleEntreprise;

    @ManyToOne
    @JoinColumn(name = "id_second_role_entreprise", nullable = false)
    SecondRoleEntreprise secondRoleEntreprise;

    @Column(nullable = false)
    private Boolean isSAOrSASMajorityManager;

    @Column(nullable = false, length = 255)
    private String autreRoleEntreprise;

    @Column(nullable = false)
    private Boolean is31PTriggered;

    @Column(nullable = false)
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
