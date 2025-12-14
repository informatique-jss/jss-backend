package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class KbisBrodcastMode {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "mode")
    private List<KbisMode> modeList;

    public KbisBrodcastMode() {
        this.modeList = Collections.singletonList(new KbisMode("T"));
    }

    public List<KbisMode> getModeList() {
        return modeList;
    }

    public void setModeList(List<KbisMode> modeList) {
        this.modeList = modeList;
    }

}
