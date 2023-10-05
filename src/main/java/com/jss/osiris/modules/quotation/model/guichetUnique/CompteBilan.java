package com.jss.osiris.modules.quotation.model.guichetUnique;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class CompteBilan {
    @Id
    @SequenceGenerator(name = "guichet_unique_compte_bilan_sequence", sequenceName = "guichet_unique_compte_bilan_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_compte_bilan_sequence")
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagination")
    private Pagination pagination;
    private boolean confidentiel;
    private Long montantCapitauxPropres;

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

    public Long getMontantCapitauxPropres() {
        return montantCapitauxPropres;
    }

    public void setMontantCapitauxPropres(Long montantCapitauxPropres) {
        this.montantCapitauxPropres = montantCapitauxPropres;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
