package com.jss.osiris.modules.quotation.model.guichetUnique;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Pagination {
    @Id
    @SequenceGenerator(name = "guichet_unique_pagination_sequence", sequenceName = "guichet_unique_pagination_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_pagination_sequence")
    private Integer id;

    @JsonProperty("from")
    private String fromPage;

    @JsonProperty("to")
    private String toPage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFromPage() {
        return fromPage;
    }

    public void setFromPage(String fromPage) {
        this.fromPage = fromPage;
    }

    public String getToPage() {
        return toPage;
    }

    public void setToPage(String toPage) {
        this.toPage = toPage;
    }

}
