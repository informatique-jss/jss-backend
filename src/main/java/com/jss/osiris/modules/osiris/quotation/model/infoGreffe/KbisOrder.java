package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisOrder {

    @JacksonXmlProperty(localName = "num_siren")
    private String numSiren;

    @JacksonXmlProperty(localName = "nic")
    private String nic;

    public KbisOrder() {
    }

    public KbisOrder(String numSiren, String nic) {
        this.numSiren = numSiren;
        this.nic = nic;
    }

    public String getNumSiren() {
        return numSiren;
    }

    public void setNumSiren(String numSiren) {
        this.numSiren = numSiren;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

}