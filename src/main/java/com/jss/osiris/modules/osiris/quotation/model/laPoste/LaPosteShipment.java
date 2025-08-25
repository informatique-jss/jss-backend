package com.jss.osiris.modules.osiris.quotation.model.laPoste;

import java.time.OffsetDateTime;
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
public class LaPosteShipment {

    @Id
    @SequenceGenerator(name = "la_poste_shipment_sequence", sequenceName = "la_poste_shipment_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "la_poste_shipment_sequence")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_la_poste_tracking")
    private LaPosteTracking laPosteTracking;

    private String urlDetail;
    private Integer holder;
    private String product;
    private Boolean isFinal;
    private OffsetDateTime entryDate;
    private OffsetDateTime deliveryDate;

    @OneToMany(mappedBy = "laPosteShipment")
    @JsonIgnoreProperties(value = { "laPosteShipment" }, allowSetters = true)
    private List<LaPosteEvent> event;

    @OneToMany(mappedBy = "laPosteShipment")
    @JsonIgnoreProperties(value = { "laPosteShipment" }, allowSetters = true)
    private List<LaPosteContext> contextData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LaPosteTracking getLaPosteTracking() {
        return laPosteTracking;
    }

    public void setLaPosteTracking(LaPosteTracking laPosteTracking) {
        this.laPosteTracking = laPosteTracking;
    }

    public String getUrlDetail() {
        return urlDetail;
    }

    public void setUrlDetail(String urlDetail) {
        this.urlDetail = urlDetail;
    }

    public Integer getHolder() {
        return holder;
    }

    public void setHolder(Integer holder) {
        this.holder = holder;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public OffsetDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(OffsetDateTime entryDate) {
        this.entryDate = entryDate;
    }

    public OffsetDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(OffsetDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public List<LaPosteEvent> getEvent() {
        return event;
    }

    public void setEvent(List<LaPosteEvent> event) {
        this.event = event;
    }

    public List<LaPosteContext> getContextData() {
        return contextData;
    }

    public void setContextData(List<LaPosteContext> contextData) {
        this.contextData = contextData;
    }

}
