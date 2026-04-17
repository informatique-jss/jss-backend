package com.jss.osiris.modules.osiris.tiers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscriber {
    @JsonProperty("prenom")
    private String prenom;

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("mail")
    private String mail;

    @JsonProperty("numero_abo")
    private String numSubscription;

    @JsonProperty("adresse")
    private String adresse;

    @JsonProperty("cp")
    private String postalCode;

    @JsonProperty("ville")
    private String city;

    @JsonProperty("societe")
    private String societeDenomination;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNumSubscription() {
        return numSubscription;
    }

    public void setNumSubscription(String numSubscription) {
        this.numSubscription = numSubscription;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSocieteDenomination() {
        return societeDenomination;
    }

    public void setSocieteDenomination(String societeDenomination) {
        this.societeDenomination = societeDenomination;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

}
