
package com.jss.osiris.modules.miscellaneous.model.EtablissementsPublics;

import java.util.List;

public class Organisme {

    public String type;
    public List<List<Feature>> features = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Feature>> getFeatures() {
        return features;
    }

    public void setFeatures(List<List<Feature>> features) {
        this.features = features;
    }

}
