
package com.jss.osiris.modules.miscellaneous.model.EtablissementsPublics;

import java.util.List;

public class Properties {

    public String id;
    public String codeInsee;
    public String pivotLocal;
    public String nom;
    public List<Adress> adresses = null;
    public List<Horaire> horaires = null;
    public String email;
    public String telephone;
    public String url;
    public Zonage zonage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodeInsee() {
        return codeInsee;
    }

    public void setCodeInsee(String codeInsee) {
        this.codeInsee = codeInsee;
    }

    public String getPivotLocal() {
        return pivotLocal;
    }

    public void setPivotLocal(String pivotLocal) {
        this.pivotLocal = pivotLocal;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Adress> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<Adress> adresses) {
        this.adresses = adresses;
    }

    public List<Horaire> getHoraires() {
        return horaires;
    }

    public void setHoraires(List<Horaire> horaires) {
        this.horaires = horaires;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Zonage getZonage() {
        return zonage;
    }

    public void setZonage(Zonage zonage) {
        this.zonage = zonage;
    }

}
