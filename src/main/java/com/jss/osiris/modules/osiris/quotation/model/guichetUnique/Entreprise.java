package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.Role;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RoleEntreprise;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutPourLaFormalite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeRepresentant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
@DoNotAudit
public class Entreprise implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role_entreprise")
    RoleEntreprise roleEntreprise;

    @Column(length = 255)
    private String pays;

    @Column(length = 255)
    private String siren;

    @Column(length = 255)
    private String registre;

    @Column(length = 255)
    private String denomination;

    @Column(length = 2000)
    private String objet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forme_juridique")
    FormeJuridique formeJuridique;

    @Column(length = 255)
    private String formeJuridiqueEtrangere;

    private LocalDate dateEffet;

    @Column(length = 255)
    private String numDetenteur;

    @Column(length = 255)
    private String numExploitant;

    @Column(length = 255)
    private String numRna;

    @Column(length = 255)
    private String lieuRegistre;

    @Column(length = 255)
    private String numGreffe;

    @Column(length = 255)
    private String entreeSortieDesChampsDeRegistre;

    @Column(length = 255)
    private String autreIdentifiantEtranger;

    @Column(length = 255)
    private String nicSiege;

    @Column(length = 255)
    private String nomCommercial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_representant")
    TypeRepresentant typeRepresentant;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_individu_representant")
    IndividuRepresentant individuRepresentant;

    String entrepriseRepresentant;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse_domicile")
    AdresseDomicile adresseEntrepriseRepresentant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_pour_la_formalite")
    StatutPourLaFormalite statutPourLaFormalite;

    @Column(length = 255)
    private String codeApe;

    private Boolean indicateurAssocieUnique;

    @Column(length = 255)
    private String nomExploitation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleEntreprise getRoleEntreprise() {
        return roleEntreprise;
    }

    public void setRoleEntreprise(RoleEntreprise roleEntreprise) {
        this.roleEntreprise = roleEntreprise;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getRegistre() {
        return registre;
    }

    public void setRegistre(String registre) {
        this.registre = registre;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public FormeJuridique getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(FormeJuridique formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public String getFormeJuridiqueEtrangere() {
        return formeJuridiqueEtrangere;
    }

    public void setFormeJuridiqueEtrangere(String formeJuridiqueEtrangere) {
        this.formeJuridiqueEtrangere = formeJuridiqueEtrangere;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public String getNumDetenteur() {
        return numDetenteur;
    }

    public void setNumDetenteur(String numDetenteur) {
        this.numDetenteur = numDetenteur;
    }

    public String getNumExploitant() {
        return numExploitant;
    }

    public void setNumExploitant(String numExploitant) {
        this.numExploitant = numExploitant;
    }

    public String getNumRna() {
        return numRna;
    }

    public void setNumRna(String numRna) {
        this.numRna = numRna;
    }

    public String getLieuRegistre() {
        return lieuRegistre;
    }

    public void setLieuRegistre(String lieuRegistre) {
        this.lieuRegistre = lieuRegistre;
    }

    public String getNumGreffe() {
        return numGreffe;
    }

    public void setNumGreffe(String numGreffe) {
        this.numGreffe = numGreffe;
    }

    public String getEntreeSortieDesChampsDeRegistre() {
        return entreeSortieDesChampsDeRegistre;
    }

    public void setEntreeSortieDesChampsDeRegistre(String entreeSortieDesChampsDeRegistre) {
        this.entreeSortieDesChampsDeRegistre = entreeSortieDesChampsDeRegistre;
    }

    public String getAutreIdentifiantEtranger() {
        return autreIdentifiantEtranger;
    }

    public void setAutreIdentifiantEtranger(String autreIdentifiantEtranger) {
        this.autreIdentifiantEtranger = autreIdentifiantEtranger;
    }

    public String getNicSiege() {
        return nicSiege;
    }

    public void setNicSiege(String nicSiege) {
        this.nicSiege = nicSiege;
    }

    public String getNomCommercial() {
        return nomCommercial;
    }

    public void setNomCommercial(String nomCommercial) {
        this.nomCommercial = nomCommercial;
    }

    public TypeRepresentant getTypeRepresentant() {
        return typeRepresentant;
    }

    public void setTypeRepresentant(TypeRepresentant typeRepresentant) {
        this.typeRepresentant = typeRepresentant;
    }

    public IndividuRepresentant getIndividuRepresentant() {
        return individuRepresentant;
    }

    public void setIndividuRepresentant(IndividuRepresentant individuRepresentant) {
        this.individuRepresentant = individuRepresentant;
    }

    public String getEntrepriseRepresentant() {
        return entrepriseRepresentant;
    }

    public void setEntrepriseRepresentant(String entrepriseRepresentant) {
        this.entrepriseRepresentant = entrepriseRepresentant;
    }

    public AdresseDomicile getAdresseEntrepriseRepresentant() {
        return adresseEntrepriseRepresentant;
    }

    public void setAdresseEntrepriseRepresentant(AdresseDomicile adresseEntrepriseRepresentant) {
        this.adresseEntrepriseRepresentant = adresseEntrepriseRepresentant;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public StatutPourLaFormalite getStatutPourLaFormalite() {
        return statutPourLaFormalite;
    }

    public void setStatutPourLaFormalite(StatutPourLaFormalite statutPourLaFormalite) {
        this.statutPourLaFormalite = statutPourLaFormalite;
    }

    public String getCodeApe() {
        return codeApe;
    }

    public void setCodeApe(String codeApe) {
        this.codeApe = codeApe;
    }

    public Boolean getIndicateurAssocieUnique() {
        return indicateurAssocieUnique;
    }

    public void setIndicateurAssocieUnique(Boolean indicateurAssocieUnique) {
        this.indicateurAssocieUnique = indicateurAssocieUnique;
    }

    public String getNomExploitation() {
        return nomExploitation;
    }

    public void setNomExploitation(String nomExploitation) {
        this.nomExploitation = nomExploitation;
    }

}
