package com.jss.osiris.modules.osiris.accounting.model.nibelis;

public class Salarie {
    private Long id_societe;
    private Long id_etablissement;
    private Long id_nibelis;
    private String matricule;
    private String nom;
    private String date_embauche;
    private String date_anciennete;
    private String date_depart;
    private String date_debut_contrat;
    private String date_fin_contrat;
    private String numero_contrat;
    private String present_periode;
    private String bulletin_periode;
    private String multi_fiche;
    private Integer ordr;

    public Long getId_societe() {
        return id_societe;
    }

    public void setId_societe(Long id_societe) {
        this.id_societe = id_societe;
    }

    public Long getId_etablissement() {
        return id_etablissement;
    }

    public void setId_etablissement(Long id_etablissement) {
        this.id_etablissement = id_etablissement;
    }

    public Long getId_nibelis() {
        return id_nibelis;
    }

    public void setId_nibelis(Long id_nibelis) {
        this.id_nibelis = id_nibelis;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate_embauche() {
        return date_embauche;
    }

    public void setDate_embauche(String date_embauche) {
        this.date_embauche = date_embauche;
    }

    public String getDate_anciennete() {
        return date_anciennete;
    }

    public void setDate_anciennete(String date_anciennete) {
        this.date_anciennete = date_anciennete;
    }

    public String getDate_depart() {
        return date_depart;
    }

    public void setDate_depart(String date_depart) {
        this.date_depart = date_depart;
    }

    public String getDate_debut_contrat() {
        return date_debut_contrat;
    }

    public void setDate_debut_contrat(String date_debut_contrat) {
        this.date_debut_contrat = date_debut_contrat;
    }

    public String getDate_fin_contrat() {
        return date_fin_contrat;
    }

    public void setDate_fin_contrat(String date_fin_contrat) {
        this.date_fin_contrat = date_fin_contrat;
    }

    public String getNumero_contrat() {
        return numero_contrat;
    }

    public void setNumero_contrat(String numero_contrat) {
        this.numero_contrat = numero_contrat;
    }

    public String getPresent_periode() {
        return present_periode;
    }

    public void setPresent_periode(String present_periode) {
        this.present_periode = present_periode;
    }

    public String getBulletin_periode() {
        return bulletin_periode;
    }

    public void setBulletin_periode(String bulletin_periode) {
        this.bulletin_periode = bulletin_periode;
    }

    public String getMulti_fiche() {
        return multi_fiche;
    }

    public void setMulti_fiche(String multi_fiche) {
        this.multi_fiche = multi_fiche;
    }

    public Integer getOrdr() {
        return ordr;
    }

    public void setOrdr(Integer ordr) {
        this.ordr = ordr;
    }
}