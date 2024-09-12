package com.jss.osiris.modules.quotation.model.guichetUnique;

import com.jss.osiris.libs.search.model.DoNotAudit;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
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
