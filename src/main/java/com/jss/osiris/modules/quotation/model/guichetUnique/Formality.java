package com.jss.osiris.modules.quotation.model.guichetUnique;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Formality {
    @Id
    @SequenceGenerator(name = "guichet_unique_formality_sequence", sequenceName = "guichet_unique_formality_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_formality_sequence")
    private Integer id;

    private String siren;
    private Content content;
    private String typePersonne;
    private boolean diffusionCommerciale;
    private String formeJuridique;

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

    public String getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(String formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

}
