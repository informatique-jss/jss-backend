package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Insaisissabilite implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_residence_principale")
    ResidencePrincipale residencePrincipale;

    @OneToMany(mappedBy = "insaisissabilite")
    @JsonIgnoreProperties(value = { "insaisissabilite" }, allowSetters = true)
    List<ResidencePrincipale> residencesSecondaires;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResidencePrincipale getResidencePrincipale() {
        return residencePrincipale;
    }

    public void setResidencePrincipale(ResidencePrincipale residencePrincipale) {
        this.residencePrincipale = residencePrincipale;
    }

    public List<ResidencePrincipale> getResidencesSecondaires() {
        return residencesSecondaires;
    }

    public void setResidencesSecondaires(List<ResidencePrincipale> residencesSecondaires) {
        this.residencesSecondaires = residencesSecondaires;
    }

}
