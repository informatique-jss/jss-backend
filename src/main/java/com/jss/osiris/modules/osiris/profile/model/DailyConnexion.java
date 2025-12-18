package com.jss.osiris.modules.osiris.profile.model;

import java.time.LocalDate;

import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_daily_connexion_date", columnList = "connexion_date"),
        @Index(name = "idx_daily_connexion_date_responsable", columnList = "id_responsable,connexion_date"),
})
public class DailyConnexion {
    @Id
    @SequenceGenerator(name = "daily_connexion_sequence", sequenceName = "daily_connexion_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_connexion_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    private Responsable responsable;

    private LocalDate connexionDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Responsable getResponsable() {
        return responsable;
    }

    public void setResponsable(Responsable responsable) {
        this.responsable = responsable;
    }

    public LocalDate getConnexionDate() {
        return connexionDate;
    }

    public void setConnexionDate(LocalDate connexionDate) {
        this.connexionDate = connexionDate;
    }

}
