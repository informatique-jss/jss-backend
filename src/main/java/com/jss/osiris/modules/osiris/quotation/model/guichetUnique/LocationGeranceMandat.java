package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeLocataireGerantMandataire;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
@Cacheable(false)
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
