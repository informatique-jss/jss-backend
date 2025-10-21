package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

public class CompteResultat implements Serializable {
    @Id
    @SequenceGenerator(name = "guichet_unique_compte_resultat_sequence", sequenceName = "guichet_unique_compte_resultat_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_compte_resultat_sequence")
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagination")
    private Pagination pagination;
    private boolean confidentiel;
    private Integer montantCA;
    private Integer resultatNet;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public boolean isConfidentiel() {
        return confidentiel;
    }

    public void setConfidentiel(boolean confidentiel) {
        this.confidentiel = confidentiel;
    }

    public Integer getMontantCA() {
        return montantCA;
    }

    public void setMontantCA(Integer montantCA) {
        this.montantCA = montantCA;
    }

    public Integer getResultatNet() {
        return resultatNet;
    }

    public void setResultatNet(Integer resultatNet) {
        this.resultatNet = resultatNet;
    }

}
