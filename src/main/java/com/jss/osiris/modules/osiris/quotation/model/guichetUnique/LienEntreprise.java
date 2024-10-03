package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.CapaciteEngagement;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.Perimetre;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RoleEntreprise;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.SecondRoleEntreprise;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutPourLaFormalite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDePersonne;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
public class LienEntreprise implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role_entreprise")
    RoleEntreprise roleEntreprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_pour_la_formalite")
    StatutPourLaFormalite statutPourLaFormalite;

    private LocalDate dateEffet;

    @OneToMany(mappedBy = "lienEntreprise")
    @JsonIgnoreProperties(value = { "lienEntreprise" }, allowSetters = true)
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

    public List<EtablissementConcerne> getEtablissementConcerne() {
        return etablissementConcerne;
    }

    public void setEtablissementConcerne(List<EtablissementConcerne> etablissementConcerne) {
        this.etablissementConcerne = etablissementConcerne;
    }

}
