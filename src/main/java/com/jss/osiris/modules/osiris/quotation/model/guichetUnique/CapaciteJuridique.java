package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TutelleCuratelle;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CapaciteJuridique implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    private Boolean statutDecede;

    @Column(length = 255)
    private String dateDeces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutelle_curatelle")
    TutelleCuratelle tutelleCuratelle;

    @Column(length = 255)
    private String DatePriseEffetCapaciteJuridique;

    private Boolean mineurEmancipe;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getStatutDecede() {
        return statutDecede;
    }

    public void setStatutDecede(Boolean statutDecede) {
        this.statutDecede = statutDecede;
    }

    public String getDateDeces() {
        return dateDeces;
    }

    public void setDateDeces(String dateDeces) {
        this.dateDeces = dateDeces;
    }

    public TutelleCuratelle getTutelleCuratelle() {
        return tutelleCuratelle;
    }

    public void setTutelleCuratelle(TutelleCuratelle tutelleCuratelle) {
        this.tutelleCuratelle = tutelleCuratelle;
    }

    public String getDatePriseEffetCapaciteJuridique() {
        return DatePriseEffetCapaciteJuridique;
    }

    public void setDatePriseEffetCapaciteJuridique(String datePriseEffetCapaciteJuridique) {
        DatePriseEffetCapaciteJuridique = datePriseEffetCapaciteJuridique;
    }

    public Boolean getMineurEmancipe() {
        return mineurEmancipe;
    }

    public void setMineurEmancipe(Boolean mineurEmancipe) {
        this.mineurEmancipe = mineurEmancipe;
    }

}
