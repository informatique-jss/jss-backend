package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.util.List;

public class ListingInfogreffe {
    private Boolean success;
    private String identifier;
    private String label;
    private String loadedAttr;

    private List<FormaliteInfogreffe> items;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean value) {
        this.success = value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String value) {
        this.identifier = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

    public String getLoadedAttr() {
        return loadedAttr;
    }

    public void setLoadedAttr(String value) {
        this.loadedAttr = value;
    }

    public List<FormaliteInfogreffe> getItems() {
        return items;
    }

    public void setItems(List<FormaliteInfogreffe> value) {
        this.items = value;
    }
}
