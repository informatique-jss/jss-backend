
package com.jss.osiris.modules.osiris.miscellaneous.model.EtablissementsPublics;

import java.util.List;

public class Adress {

    public String type;
    public List<String> lignes = null;
    public String codePostal;
    public String commune;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLignes() {
        return lignes;
    }

    public void setLignes(List<String> lignes) {
        this.lignes = lignes;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

}
