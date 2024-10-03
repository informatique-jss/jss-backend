package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RoleContrat;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutContrat;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TaciteReconduction;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypePersonneContractante;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
public class ContratDAppui implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role_contrat")
    RoleContrat roleContrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_contrat")
    StatutContrat statutContrat;

    private LocalDate dateDebutContrat;

    private LocalDate dateFinContrat;

    private LocalDate dateEffetContrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tacite_reconduction")
    TaciteReconduction taciteReconduction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_personne_contractante")
    TypePersonneContractante typePersonneContractante;

    private Boolean resiliationContrat;

    private Boolean indicateurRenouvellementContrat;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contractant")
    Repreneur contractant;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    Entreprise entreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse")
    AdresseDomicile adresse;

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

    public Repreneur getContractant() {
        return contractant;
    }

    public void setContractant(Repreneur contractant) {
        this.contractant = contractant;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public AdresseDomicile getAdresse() {
        return adresse;
    }

    public void setAdresse(AdresseDomicile adresse) {
        this.adresse = adresse;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

}
