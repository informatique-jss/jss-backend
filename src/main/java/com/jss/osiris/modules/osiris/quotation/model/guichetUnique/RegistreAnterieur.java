package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class RegistreAnterieur {

    private TypeRegistre raa;
    private TypeRegistre rnm;
    private TypeRegistre rncs;

    public TypeRegistre getRaa() {
        return raa;
    }

    public void setRaa(TypeRegistre raa) {
        this.raa = raa;
    }

    public TypeRegistre getRnm() {
        return rnm;
    }

    public void setRnm(TypeRegistre rnm) {
        this.rnm = rnm;
    }

    public TypeRegistre getRncs() {
        return rncs;
    }

    public void setRncs(TypeRegistre rncs) {
        this.rncs = rncs;
    }

}
