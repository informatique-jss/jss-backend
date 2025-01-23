
package com.jss.osiris.modules.osiris.miscellaneous.model.EtablissementsPublics;

import java.util.List;

public class Horaire {

    public String du;
    public String au;
    public List<Heure> heures = null;

    public String getDu() {
        return du;
    }

    public void setDu(String du) {
        this.du = du;
    }

    public String getAu() {
        return au;
    }

    public void setAu(String au) {
        this.au = au;
    }

    public List<Heure> getHeures() {
        return heures;
    }

    public void setHeures(List<Heure> heures) {
        this.heures = heures;
    }

}
