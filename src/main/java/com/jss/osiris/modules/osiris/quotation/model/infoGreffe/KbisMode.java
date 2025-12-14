package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisMode {

    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String type;

    public KbisMode() {
    }

    public KbisMode(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
