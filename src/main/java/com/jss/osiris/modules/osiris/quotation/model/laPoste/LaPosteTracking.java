package com.jss.osiris.modules.osiris.quotation.model.laPoste;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(indexes = { @Index(name = "idx_la_poste_tracking_ship", columnList = "idShip", unique = true),
})
public class LaPosteTracking {
    @Id
    @SequenceGenerator(name = "la_poste_tracking_sequence", sequenceName = "la_poste_tracking_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "la_poste_tracking_sequence")
    private Integer id;

    private String idShip;
    private String lang;
    private String scope;
    private Integer returnCode;
    private String returnMessage;

    @OneToMany(mappedBy = "laPosteTracking")
    @JsonIgnoreProperties(value = { "laPosteTracking" }, allowSetters = true)
    private List<LaPosteShipment> shipment;

    @ManyToOne
    @JoinColumn(name = "id_provision")
    private Provision provision;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public String getIdShip() {
        return idShip;
    }

    public void setIdShip(String idShip) {
        this.idShip = idShip;
    }

    public List<LaPosteShipment> getShipment() {
        return shipment;
    }

    public void setShipment(List<LaPosteShipment> shipment) {
        this.shipment = shipment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Provision getProvision() {
        return provision;
    }

    public void setProvision(Provision provision) {
        this.provision = provision;
    }

}
