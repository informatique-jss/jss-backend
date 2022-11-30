package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class GuichetUniqueSearch {

    @JsonProperty("@context")
    public String context;
    @JsonProperty("@id")
    public String id;
    @JsonProperty("@type")
    public String type;
    @JsonProperty("hydra:member")
    public List<Formalite> hydraMember = null;
    @JsonProperty("hydra:totalItems")
    public Integer hydraTotalItems;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Formalite> getHydraMember() {
        return hydraMember;
    }

    public void setHydraMember(List<Formalite> hydraMember) {
        this.hydraMember = hydraMember;
    }

    public Integer getHydraTotalItems() {
        return hydraTotalItems;
    }

    public void setHydraTotalItems(Integer hydraTotalItems) {
        this.hydraTotalItems = hydraTotalItems;
    }

}