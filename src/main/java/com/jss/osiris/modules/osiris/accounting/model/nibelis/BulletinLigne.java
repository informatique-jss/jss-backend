package com.jss.osiris.modules.osiris.accounting.model.nibelis;

public class BulletinLigne {
    private Integer ordre;
    private Integer id_rubrique;
    private Integer code_rubrique;
    private String type_rubrique;
    private String type_rubrique_libelle;
    private String libelle;
    private Double nombre;
    private Double base;
    private Double taux_salariale;
    private Double montant_salariale;
    private Double taux_patronale;
    private Double montant_patronale;
    private Double coefficient_patronale;

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public Integer getId_rubrique() {
        return id_rubrique;
    }

    public void setId_rubrique(Integer id_rubrique) {
        this.id_rubrique = id_rubrique;
    }

    public Integer getCode_rubrique() {
        return code_rubrique;
    }

    public void setCode_rubrique(Integer code_rubrique) {
        this.code_rubrique = code_rubrique;
    }

    public String getType_rubrique() {
        return type_rubrique;
    }

    public void setType_rubrique(String type_rubrique) {
        this.type_rubrique = type_rubrique;
    }

    public String getType_rubrique_libelle() {
        return type_rubrique_libelle;
    }

    public void setType_rubrique_libelle(String type_rubrique_libelle) {
        this.type_rubrique_libelle = type_rubrique_libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Double getNombre() {
        return nombre;
    }

    public void setNombre(Double nombre) {
        this.nombre = nombre;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public Double getTaux_salariale() {
        return taux_salariale;
    }

    public void setTaux_salariale(Double taux_salariale) {
        this.taux_salariale = taux_salariale;
    }

    public Double getMontant_salariale() {
        return montant_salariale;
    }

    public void setMontant_salariale(Double montant_salariale) {
        this.montant_salariale = montant_salariale;
    }

    public Double getTaux_patronale() {
        return taux_patronale;
    }

    public void setTaux_patronale(Double taux_patronale) {
        this.taux_patronale = taux_patronale;
    }

    public Double getMontant_patronale() {
        return montant_patronale;
    }

    public void setMontant_patronale(Double montant_patronale) {
        this.montant_patronale = montant_patronale;
    }

    public Double getCoefficient_patronale() {
        return coefficient_patronale;
    }

    public void setCoefficient_patronale(Double coefficient_patronale) {
        this.coefficient_patronale = coefficient_patronale;
    }
}