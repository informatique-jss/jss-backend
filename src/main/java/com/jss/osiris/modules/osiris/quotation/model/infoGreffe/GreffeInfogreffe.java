package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class GreffeInfogreffe implements Serializable {
    @Id
    private String numero;
    private String nom;
    private String codeGroupement;
    private String codeEDI;
    private String typeTribunalReel;
    private String nomGreffeMin;
    private String codeLiasseDGFIP;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCodeGroupement() {
        return codeGroupement;
    }

    public void setCodeGroupement(String codeGroupement) {
        this.codeGroupement = codeGroupement;
    }

    public String getCodeEDI() {
        return codeEDI;
    }

    public void setCodeEDI(String codeEDI) {
        this.codeEDI = codeEDI;
    }

    public String getTypeTribunalReel() {
        return typeTribunalReel;
    }

    public void setTypeTribunalReel(String typeTribunalReel) {
        this.typeTribunalReel = typeTribunalReel;
    }

    public String getNomGreffeMin() {
        return nomGreffeMin;
    }

    public void setNomGreffeMin(String nomGreffeMin) {
        this.nomGreffeMin = nomGreffeMin;
    }

    public String getCodeLiasseDGFIP() {
        return codeLiasseDGFIP;
    }

    public void setCodeLiasseDGFIP(String codeLiasseDGFIP) {
        this.codeLiasseDGFIP = codeLiasseDGFIP;
    }

}
