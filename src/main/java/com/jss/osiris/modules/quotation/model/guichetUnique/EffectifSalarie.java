package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class EffectifSalarie implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Boolean presenceSalarie;

    @Column(nullable = false)
    private Integer nombreSalarie;

    @Column(nullable = false)
    private Integer nombreApprenti;

    @Column(nullable = false)
    private Integer nombreVrp;

    @Column(nullable = false)
    private Boolean emploiPremierSalarie;

    @Column(nullable = false)
    private LocalDate dateEffetFinEmploiSalarie;

    @Column(nullable = false)
    private Integer nombreSaisonnier;

    @Column(nullable = false)
    private Integer nombreSalariesMarin;

    @Column(nullable = false)
    private Boolean employeurSalarieNonRegimeFr;

    @Column(nullable = false)
    private LocalDate dateEffetDebutEmploiSalarie;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @Column(nullable = false)
    private LocalDate dateCessationEmploiSalaries;

    @Column(nullable = false)
    private Boolean finEmploiSalarie;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getPresenceSalarie() {
        return presenceSalarie;
    }

    public void setPresenceSalarie(Boolean presenceSalarie) {
        this.presenceSalarie = presenceSalarie;
    }

    public Integer getNombreSalarie() {
        return nombreSalarie;
    }

    public void setNombreSalarie(Integer nombreSalarie) {
        this.nombreSalarie = nombreSalarie;
    }

    public Integer getNombreApprenti() {
        return nombreApprenti;
    }

    public void setNombreApprenti(Integer nombreApprenti) {
        this.nombreApprenti = nombreApprenti;
    }

    public Integer getNombreVrp() {
        return nombreVrp;
    }

    public void setNombreVrp(Integer nombreVrp) {
        this.nombreVrp = nombreVrp;
    }

    public Boolean getEmploiPremierSalarie() {
        return emploiPremierSalarie;
    }

    public void setEmploiPremierSalarie(Boolean emploiPremierSalarie) {
        this.emploiPremierSalarie = emploiPremierSalarie;
    }

    public LocalDate getDateEffetFinEmploiSalarie() {
        return dateEffetFinEmploiSalarie;
    }

    public void setDateEffetFinEmploiSalarie(LocalDate dateEffetFinEmploiSalarie) {
        this.dateEffetFinEmploiSalarie = dateEffetFinEmploiSalarie;
    }

    public Integer getNombreSaisonnier() {
        return nombreSaisonnier;
    }

    public void setNombreSaisonnier(Integer nombreSaisonnier) {
        this.nombreSaisonnier = nombreSaisonnier;
    }

    public Integer getNombreSalariesMarin() {
        return nombreSalariesMarin;
    }

    public void setNombreSalariesMarin(Integer nombreSalariesMarin) {
        this.nombreSalariesMarin = nombreSalariesMarin;
    }

    public Boolean getEmployeurSalarieNonRegimeFr() {
        return employeurSalarieNonRegimeFr;
    }

    public void setEmployeurSalarieNonRegimeFr(Boolean employeurSalarieNonRegimeFr) {
        this.employeurSalarieNonRegimeFr = employeurSalarieNonRegimeFr;
    }

    public LocalDate getDateEffetDebutEmploiSalarie() {
        return dateEffetDebutEmploiSalarie;
    }

    public void setDateEffetDebutEmploiSalarie(LocalDate dateEffetDebutEmploiSalarie) {
        this.dateEffetDebutEmploiSalarie = dateEffetDebutEmploiSalarie;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public LocalDate getDateCessationEmploiSalaries() {
        return dateCessationEmploiSalaries;
    }

    public void setDateCessationEmploiSalaries(LocalDate dateCessationEmploiSalaries) {
        this.dateCessationEmploiSalaries = dateCessationEmploiSalaries;
    }

    public Boolean getFinEmploiSalarie() {
        return finEmploiSalarie;
    }

    public void setFinEmploiSalarie(Boolean finEmploiSalarie) {
        this.finEmploiSalarie = finEmploiSalarie;
    }

}
