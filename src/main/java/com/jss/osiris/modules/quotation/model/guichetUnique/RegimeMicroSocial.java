package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PeriodiciteVersement;

@Entity
public class RegimeMicroSocial implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
