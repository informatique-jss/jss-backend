package com.jss.osiris.modules.quotation.model.guichetUnique;

import com.jss.osiris.libs.search.model.DoNotAudit;

import jakarta.persistence.CascadeType;
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
public class Rate {

    @Id
    @SequenceGenerator(name = "guichet_unique_rate_sequence", sequenceName = "guichet_unique_rate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_rate_sequence")
    private Integer id;

    private long amount;
    private long htAmount;
    private String code;
    private String label;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partenaire")
    private Partenaire partenaire;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getHtAmount() {
        return htAmount;
    }

    public void setHtAmount(long htAmount) {
        this.htAmount = htAmount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Partenaire getPartenaire() {
        return partenaire;
    }

    public void setPartenaire(Partenaire partenaire) {
        this.partenaire = partenaire;
    }
}
