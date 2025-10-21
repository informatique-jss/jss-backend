package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RegistreEirlDeLancienneEirl;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

public class AncienneEIRL implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @Column(length = 255)
    private String identificationSiren;

    @Column(length = 255)
    private String ancienneDenominationEIRL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registre_eirl_de_lancienne_eirl")
    RegistreEirlDeLancienneEirl registreEirlDeLancienneEirl;

    @Column(length = 255)
    private String lieuImmatriculationAncienneEIRL;

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
