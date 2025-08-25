package com.jss.osiris.modules.osiris.quotation.model.laPoste;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class LaPosteContext {

    @Id
    @SequenceGenerator(name = "la_poste_context_sequence", sequenceName = "la_poste_context_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "la_poste_context_sequence")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_la_poste_shipment")
    private LaPosteShipment laPosteShipment;

    @OneToMany(mappedBy = "laPosteContext")
    @JsonIgnoreProperties(value = { "laPosteContext" }, allowSetters = true)
    private List<LaPosteRemovalPoint> removalPoint;

    private String originCountry;
    private String arrivalCountry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LaPosteShipment getLaPosteShipment() {
        return laPosteShipment;
    }

    public void setLaPosteShipment(LaPosteShipment laPosteShipment) {
        this.laPosteShipment = laPosteShipment;
    }

    public List<LaPosteRemovalPoint> getRemovalPoint() {
        return removalPoint;
    }

    public void setRemovalPoint(List<LaPosteRemovalPoint> removalPoint) {
        this.removalPoint = removalPoint;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getArrivalCountry() {
        return arrivalCountry;
    }

    public void setArrivalCountry(String arrivalCountry) {
        this.arrivalCountry = arrivalCountry;
    }

}