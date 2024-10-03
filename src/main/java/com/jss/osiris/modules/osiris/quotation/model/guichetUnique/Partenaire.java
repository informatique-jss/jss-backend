package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import com.jss.osiris.libs.search.model.DoNotAudit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
public class Partenaire {

    @Id
    @SequenceGenerator(name = "guichet_unique_partenaire_sequence", sequenceName = "guichet_unique_partenaire_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_partenaire_sequence")
    private Integer id;

    private String codifNorme;
    private String libelleCourt;
    private String denomination;
    private boolean isInpi;
    private String created;
    private String updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodifNorme() {
        return codifNorme;
    }

    public void setCodifNorme(String codifNorme) {
        this.codifNorme = codifNorme;
    }

    public String getLibelleCourt() {
        return libelleCourt;
    }

    public void setLibelleCourt(String libelleCourt) {
        this.libelleCourt = libelleCourt;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public boolean isInpi() {
        return isInpi;
    }

    public void setInpi(boolean isInpi) {
        this.isInpi = isInpi;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

}
