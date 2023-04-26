package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

public class Rna implements Serializable {
    public Association association;

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

}