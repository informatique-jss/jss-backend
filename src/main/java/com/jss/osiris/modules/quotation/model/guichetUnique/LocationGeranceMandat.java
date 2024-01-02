package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeLocataireGerantMandataire;

@Entity
public class LocationGeranceMandat implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @Column(length = 255)
    private String destinationLocationGeranceMandat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_locataire_gerant_mandataire")
    TypeLocataireGerantMandataire typeLocataireGerantMandataire;

    private LocalDate dateEffet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDestinationLocationGeranceMandat() {
        return destinationLocationGeranceMandat;
    }

    public void setDestinationLocationGeranceMandat(String destinationLocationGeranceMandat) {
        this.destinationLocationGeranceMandat = destinationLocationGeranceMandat;
    }

    public TypeLocataireGerantMandataire getTypeLocataireGerantMandataire() {
        return typeLocataireGerantMandataire;
    }

    public void setTypeLocataireGerantMandataire(TypeLocataireGerantMandataire typeLocataireGerantMandataire) {
        this.typeLocataireGerantMandataire = typeLocataireGerantMandataire;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

}
