package com.jss.osiris.modules.osiris.quotation.model.laPoste;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class LaPosteEvent {

    @Id
    @SequenceGenerator(name = "la_poste_event_sequence", sequenceName = "la_poste_event_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "la_poste_event_sequence")
    private Integer id;

    private OffsetDateTime date;
    private String label;
    private String code;

    @ManyToOne
    @JoinColumn(name = "id_la_poste_shipment")
    private LaPosteShipment laPosteShipment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LaPosteShipment getLaPosteShipment() {
        return laPosteShipment;
    }

    public void setLaPosteShipment(LaPosteShipment laPosteShipment) {
        this.laPosteShipment = laPosteShipment;
    }

}