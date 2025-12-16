package com.jss.osiris.modules.osiris.quotation.service.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisArg {
    @JacksonXmlProperty(isAttribute = true, localName = "xsi:type")
    private String type = "xsd:string";

    // On retire l'annotation @JacksonXmlText ici pour éviter l'encodage auto
    private String content;

    public KbisArg(String content) {
        this.content = content;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

}