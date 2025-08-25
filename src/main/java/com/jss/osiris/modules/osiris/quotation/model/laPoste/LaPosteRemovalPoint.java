package com.jss.osiris.modules.osiris.quotation.model.laPoste;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class LaPosteRemovalPoint {

    @Id
    @SequenceGenerator(name = "la_poste_removal_point_sequence", sequenceName = "la_poste_removal_point_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "la_poste_removal_point_sequence")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_la_poste_context")
    private LaPosteContext laPosteContext;

    private String name;
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LaPosteContext getLaPosteContext() {
        return laPosteContext;
    }

    public void setLaPosteContext(LaPosteContext laPosteContext) {
        this.laPosteContext = laPosteContext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}