package com.jss.osiris.modules.osiris.miscellaneous.model.EtablissementsPublics;

import java.util.List;

public class GeoCity {
    private List<String> codesPostaux;
    private String nom;
    private String code;

    public List<String> getCodesPostaux() {
        return codesPostaux;
    }

    public void setCodesPostaux(List<String> codesPostaux) {
        this.codesPostaux = codesPostaux;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
