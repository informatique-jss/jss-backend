package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;

@JsonIgnoreProperties
public class Formality {
    private String siren;
    private Content content;
    private String typePersonne;
    private boolean diffusionCommerciale;
    private FormeJuridique formeJuridique;
    private ArrayList<Historique> historique;

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(String typePersonne) {
        this.typePersonne = typePersonne;
    }

    public boolean isDiffusionCommerciale() {
        return diffusionCommerciale;
    }

    public void setDiffusionCommerciale(boolean diffusionCommerciale) {
        this.diffusionCommerciale = diffusionCommerciale;
    }

    public ArrayList<Historique> getHistorique() {
        return historique;
    }

    public void setHistorique(ArrayList<Historique> historique) {
        this.historique = historique;
    }

    public FormeJuridique getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(FormeJuridique formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

}
