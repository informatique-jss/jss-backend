package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegistreEirlDeLancienneEirl;

@Entity
public class AncienneEIRL implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String identificationSiren;

    @Column(nullable = false, length = 255)
    private String ancienneDenominationEIRL;

    @ManyToOne
    @JoinColumn(name = "id_registre_eirl_de_lancienne_eirl", nullable = false)
    RegistreEirlDeLancienneEirl registreEirlDeLancienneEirl;

    @Column(nullable = false, length = 255)
    private String lieuImmatriculationAncienneEIRL;

    @Column(nullable = false)
    private LocalDate dateEffet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentificationSiren() {
        return identificationSiren;
    }

    public void setIdentificationSiren(String identificationSiren) {
        this.identificationSiren = identificationSiren;
    }

    public String getAncienneDenominationEIRL() {
        return ancienneDenominationEIRL;
    }

    public void setAncienneDenominationEIRL(String ancienneDenominationEIRL) {
        this.ancienneDenominationEIRL = ancienneDenominationEIRL;
    }

    public RegistreEirlDeLancienneEirl getRegistreEirlDeLancienneEirl() {
        return registreEirlDeLancienneEirl;
    }

    public void setRegistreEirlDeLancienneEirl(RegistreEirlDeLancienneEirl registreEirlDeLancienneEirl) {
        this.registreEirlDeLancienneEirl = registreEirlDeLancienneEirl;
    }

    public String getLieuImmatriculationAncienneEIRL() {
        return lieuImmatriculationAncienneEIRL;
    }

    public void setLieuImmatriculationAncienneEIRL(String lieuImmatriculationAncienneEIRL) {
        this.lieuImmatriculationAncienneEIRL = lieuImmatriculationAncienneEIRL;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

}
