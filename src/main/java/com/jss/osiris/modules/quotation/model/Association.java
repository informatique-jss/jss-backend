package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;

public class Association {
    public int id;
    public String is_waldec;
    public String id_association;
    public String id_ex_association;
    public Object siret;
    public Object numero_reconnaissance_utilite_publique;
    public String code_gestion;
    public String date_creation;
    public String date_derniere_declaration;
    public String date_publication_creation;
    public Object date_declaration_dissolution;
    public String nature;
    public String groupement;
    public String titre;
    public String titre_court;
    public String objet;
    public String objet_social1;
    public String objet_social2;
    public Object l1_adresse_import;
    public Object l2_adresse_import;
    public Object l3_adresse_import;
    public Object adresse_siege;
    public String adresse_numero_voie;
    public Object adresse_repetition;
    public String adresse_type_voie;
    public String adresse_libelle_voie;
    public String adresse_distribution;
    public String adresse_code_insee;
    public String adresse_code_postal;
    public String adresse_libelle_commune;
    public Object adresse_gestion_nom;
    public Object adresse_gestion_format_postal;
    public Object adresse_gestion_geo;
    public String adresse_gestion_libelle_voie;
    public String adresse_gestion_distribution;
    public String adresse_gestion_code_postal;
    public String adresse_gestion_acheminement;
    public String adresse_gestion_pays;
    public String dirigeant_civilite;
    public Object telephone;
    public Object site_web;
    public Object email;
    public String autorisation_publication_web;
    public Object observation;
    public String position_activite;
    public String derniere_maj;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    public LocalDateTime created_at;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    public LocalDateTime updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIs_waldec() {
        return is_waldec;
    }

    public void setIs_waldec(String is_waldec) {
        this.is_waldec = is_waldec;
    }

    public String getId_association() {
        return id_association;
    }

    public void setId_association(String id_association) {
        this.id_association = id_association;
    }

    public String getId_ex_association() {
        return id_ex_association;
    }

    public void setId_ex_association(String id_ex_association) {
        this.id_ex_association = id_ex_association;
    }

    public Object getSiret() {
        return siret;
    }

    public void setSiret(Object siret) {
        this.siret = siret;
    }

    public Object getNumero_reconnaissance_utilite_publique() {
        return numero_reconnaissance_utilite_publique;
    }

    public void setNumero_reconnaissance_utilite_publique(Object numero_reconnaissance_utilite_publique) {
        this.numero_reconnaissance_utilite_publique = numero_reconnaissance_utilite_publique;
    }

    public String getCode_gestion() {
        return code_gestion;
    }

    public void setCode_gestion(String code_gestion) {
        this.code_gestion = code_gestion;
    }

    public String getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(String date_creation) {
        this.date_creation = date_creation;
    }

    public String getDate_derniere_declaration() {
        return date_derniere_declaration;
    }

    public void setDate_derniere_declaration(String date_derniere_declaration) {
        this.date_derniere_declaration = date_derniere_declaration;
    }

    public String getDate_publication_creation() {
        return date_publication_creation;
    }

    public void setDate_publication_creation(String date_publication_creation) {
        this.date_publication_creation = date_publication_creation;
    }

    public Object getDate_declaration_dissolution() {
        return date_declaration_dissolution;
    }

    public void setDate_declaration_dissolution(Object date_declaration_dissolution) {
        this.date_declaration_dissolution = date_declaration_dissolution;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getGroupement() {
        return groupement;
    }

    public void setGroupement(String groupement) {
        this.groupement = groupement;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getTitre_court() {
        return titre_court;
    }

    public void setTitre_court(String titre_court) {
        this.titre_court = titre_court;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getObjet_social1() {
        return objet_social1;
    }

    public void setObjet_social1(String objet_social1) {
        this.objet_social1 = objet_social1;
    }

    public String getObjet_social2() {
        return objet_social2;
    }

    public void setObjet_social2(String objet_social2) {
        this.objet_social2 = objet_social2;
    }

    public Object getL1_adresse_import() {
        return l1_adresse_import;
    }

    public void setL1_adresse_import(Object l1_adresse_import) {
        this.l1_adresse_import = l1_adresse_import;
    }

    public Object getL2_adresse_import() {
        return l2_adresse_import;
    }

    public void setL2_adresse_import(Object l2_adresse_import) {
        this.l2_adresse_import = l2_adresse_import;
    }

    public Object getL3_adresse_import() {
        return l3_adresse_import;
    }

    public void setL3_adresse_import(Object l3_adresse_import) {
        this.l3_adresse_import = l3_adresse_import;
    }

    public Object getAdresse_siege() {
        return adresse_siege;
    }

    public void setAdresse_siege(Object adresse_siege) {
        this.adresse_siege = adresse_siege;
    }

    public String getAdresse_numero_voie() {
        return adresse_numero_voie;
    }

    public void setAdresse_numero_voie(String adresse_numero_voie) {
        this.adresse_numero_voie = adresse_numero_voie;
    }

    public Object getAdresse_repetition() {
        return adresse_repetition;
    }

    public void setAdresse_repetition(Object adresse_repetition) {
        this.adresse_repetition = adresse_repetition;
    }

    public String getAdresse_type_voie() {
        return adresse_type_voie;
    }

    public void setAdresse_type_voie(String adresse_type_voie) {
        this.adresse_type_voie = adresse_type_voie;
    }

    public String getAdresse_libelle_voie() {
        return adresse_libelle_voie;
    }

    public void setAdresse_libelle_voie(String adresse_libelle_voie) {
        this.adresse_libelle_voie = adresse_libelle_voie;
    }

    public String getAdresse_distribution() {
        return adresse_distribution;
    }

    public void setAdresse_distribution(String adresse_distribution) {
        this.adresse_distribution = adresse_distribution;
    }

    public String getAdresse_code_insee() {
        return adresse_code_insee;
    }

    public void setAdresse_code_insee(String adresse_code_insee) {
        this.adresse_code_insee = adresse_code_insee;
    }

    public String getAdresse_code_postal() {
        return adresse_code_postal;
    }

    public void setAdresse_code_postal(String adresse_code_postal) {
        this.adresse_code_postal = adresse_code_postal;
    }

    public String getAdresse_libelle_commune() {
        return adresse_libelle_commune;
    }

    public void setAdresse_libelle_commune(String adresse_libelle_commune) {
        this.adresse_libelle_commune = adresse_libelle_commune;
    }

    public Object getAdresse_gestion_nom() {
        return adresse_gestion_nom;
    }

    public void setAdresse_gestion_nom(Object adresse_gestion_nom) {
        this.adresse_gestion_nom = adresse_gestion_nom;
    }

    public Object getAdresse_gestion_format_postal() {
        return adresse_gestion_format_postal;
    }

    public void setAdresse_gestion_format_postal(Object adresse_gestion_format_postal) {
        this.adresse_gestion_format_postal = adresse_gestion_format_postal;
    }

    public Object getAdresse_gestion_geo() {
        return adresse_gestion_geo;
    }

    public void setAdresse_gestion_geo(Object adresse_gestion_geo) {
        this.adresse_gestion_geo = adresse_gestion_geo;
    }

    public String getAdresse_gestion_libelle_voie() {
        return adresse_gestion_libelle_voie;
    }

    public void setAdresse_gestion_libelle_voie(String adresse_gestion_libelle_voie) {
        this.adresse_gestion_libelle_voie = adresse_gestion_libelle_voie;
    }

    public String getAdresse_gestion_distribution() {
        return adresse_gestion_distribution;
    }

    public void setAdresse_gestion_distribution(String adresse_gestion_distribution) {
        this.adresse_gestion_distribution = adresse_gestion_distribution;
    }

    public String getAdresse_gestion_code_postal() {
        return adresse_gestion_code_postal;
    }

    public void setAdresse_gestion_code_postal(String adresse_gestion_code_postal) {
        this.adresse_gestion_code_postal = adresse_gestion_code_postal;
    }

    public String getAdresse_gestion_acheminement() {
        return adresse_gestion_acheminement;
    }

    public void setAdresse_gestion_acheminement(String adresse_gestion_acheminement) {
        this.adresse_gestion_acheminement = adresse_gestion_acheminement;
    }

    public String getAdresse_gestion_pays() {
        return adresse_gestion_pays;
    }

    public void setAdresse_gestion_pays(String adresse_gestion_pays) {
        this.adresse_gestion_pays = adresse_gestion_pays;
    }

    public String getDirigeant_civilite() {
        return dirigeant_civilite;
    }

    public void setDirigeant_civilite(String dirigeant_civilite) {
        this.dirigeant_civilite = dirigeant_civilite;
    }

    public Object getTelephone() {
        return telephone;
    }

    public void setTelephone(Object telephone) {
        this.telephone = telephone;
    }

    public Object getSite_web() {
        return site_web;
    }

    public void setSite_web(Object site_web) {
        this.site_web = site_web;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public String getAutorisation_publication_web() {
        return autorisation_publication_web;
    }

    public void setAutorisation_publication_web(String autorisation_publication_web) {
        this.autorisation_publication_web = autorisation_publication_web;
    }

    public Object getObservation() {
        return observation;
    }

    public void setObservation(Object observation) {
        this.observation = observation;
    }

    public String getPosition_activite() {
        return position_activite;
    }

    public void setPosition_activite(String position_activite) {
        this.position_activite = position_activite;
    }

    public String getDerniere_maj() {
        return derniere_maj;
    }

    public void setDerniere_maj(String derniere_maj) {
        this.derniere_maj = derniere_maj;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

}
