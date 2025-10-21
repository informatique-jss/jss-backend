package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.DoNotAudit;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DoNotAudit
@Cacheable(false)
public class Partenaire implements Serializable {

    @Id
    private String codifNorme;
    private String libelleCourt;
    private String denomination;
    private boolean isInpi;

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

}
