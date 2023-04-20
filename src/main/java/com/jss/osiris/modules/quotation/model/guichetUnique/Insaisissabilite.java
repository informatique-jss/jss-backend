package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Insaisissabilite implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
