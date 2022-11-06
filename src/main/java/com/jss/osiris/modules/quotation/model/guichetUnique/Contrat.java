package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleContrat;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutContrat;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TaciteReconduction;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonneContractante;

@Entity
public class Contrat implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_role_contrat", nullable = false)
    RoleContrat roleContrat;

    @ManyToOne
    @JoinColumn(name = "id_statut_contrat", nullable = false)
    StatutContrat statutContrat;

    @Column(nullable = false)
    private LocalDate dateDebutContrat;

    @Column(nullable = false)
    private LocalDate dateFinContrat;

    @Column(nullable = false)
    private LocalDate dateEffetContrat;

    @ManyToOne
    @JoinColumn(name = "id_tacite_reconduction", nullable = false)
    TaciteReconduction taciteReconduction;

    @ManyToOne
    @JoinColumn(name = "id_type_personne_contractante", nullable = false)
    TypePersonneContractante typePersonneContractante;

    @Column(nullable = false)
    private Boolean resiliationContrat;

    @Column(nullable = false)
    private Boolean indicateurRenouvellementContrat;

    @ManyToOne
    @JoinColumn(name = "id_contractant", nullable = false)
    Contractant contractant;

    @ManyToOne
    @JoinColumn(name = "id_entreprise", nullable = false)
    Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "id_adresse", nullable = false)
    Immeuble adresse;

    @Column(nullable = false)
    private LocalDate dateEffet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleContrat getRoleContrat() {
        return roleContrat;
    }

    public void setRoleContrat(RoleContrat roleContrat) {
        this.roleContrat = roleContrat;
    }

    public StatutContrat getStatutContrat() {
        return statutContrat;
    }

    public void setStatutContrat(StatutContrat statutContrat) {
        this.statutContrat = statutContrat;
    }

    public LocalDate getDateDebutContrat() {
        return dateDebutContrat;
    }

    public void setDateDebutContrat(LocalDate dateDebutContrat) {
        this.dateDebutContrat = dateDebutContrat;
    }

    public LocalDate getDateFinContrat() {
        return dateFinContrat;
    }

    public void setDateFinContrat(LocalDate dateFinContrat) {
        this.dateFinContrat = dateFinContrat;
    }

    public LocalDate getDateEffetContrat() {
        return dateEffetContrat;
    }

    public void setDateEffetContrat(LocalDate dateEffetContrat) {
        this.dateEffetContrat = dateEffetContrat;
    }

    public TaciteReconduction getTaciteReconduction() {
        return taciteReconduction;
    }

    public void setTaciteReconduction(TaciteReconduction taciteReconduction) {
        this.taciteReconduction = taciteReconduction;
    }

    public TypePersonneContractante getTypePersonneContractante() {
        return typePersonneContractante;
    }

    public void setTypePersonneContractante(TypePersonneContractante typePersonneContractante) {
        this.typePersonneContractante = typePersonneContractante;
    }

    public Boolean getResiliationContrat() {
        return resiliationContrat;
    }

    public void setResiliationContrat(Boolean resiliationContrat) {
        this.resiliationContrat = resiliationContrat;
    }

    public Boolean getIndicateurRenouvellementContrat() {
        return indicateurRenouvellementContrat;
    }

    public void setIndicateurRenouvellementContrat(Boolean indicateurRenouvellementContrat) {
        this.indicateurRenouvellementContrat = indicateurRenouvellementContrat;
    }

    public Contractant getContractant() {
        return contractant;
    }

    public void setContractant(Contractant contractant) {
        this.contractant = contractant;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public Immeuble getAdresse() {
        return adresse;
    }

    public void setAdresse(Immeuble adresse) {
        this.adresse = adresse;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

}
