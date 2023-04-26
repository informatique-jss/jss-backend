package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;

public class Siren implements Serializable {
    private Header header;
    private UniteLegale uniteLegale;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public UniteLegale getUniteLegale() {
        return uniteLegale;
    }

    public void setUniteLegale(UniteLegale uniteLegale) {
        this.uniteLegale = uniteLegale;
    }

}
