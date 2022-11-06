package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TutelleCuratelle;

@Entity
public class CapaciteJuridique implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Boolean statutDecede;

    @Column(nullable = false, length = 255)
    private String dateDeces;

    @ManyToOne
    @JoinColumn(name = "id_tutelle_curatelle", nullable = false)
    TutelleCuratelle tutelleCuratelle;

    @Column(nullable = false, length = 255)
    private String DatePriseEffetCapaciteJuridique;

    @Column(nullable = false)
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
