package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.PeriodiciteVersement;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties
public class RegimeMicroSocial implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    private Boolean optionMicroSocial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodicite_versement")
    PeriodiciteVersement periodiciteVersement;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getOptionMicroSocial() {
        return optionMicroSocial;
    }

    public void setOptionMicroSocial(Boolean optionMicroSocial) {
        this.optionMicroSocial = optionMicroSocial;
    }

    public PeriodiciteVersement getPeriodiciteVersement() {
        return periodiciteVersement;
    }

    public void setPeriodiciteVersement(PeriodiciteVersement periodiciteVersement) {
        this.periodiciteVersement = periodiciteVersement;
    }

}
