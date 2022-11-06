package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DiffusionINSEE;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;

@Entity
public class Formalite implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Integer formalityDraftId;

    @Column(nullable = false, length = 255)
    private String companyName;

    @ManyToOne
    @JoinColumn(name = "id_content", nullable = false)
    Content content;

    @Column(nullable = false, length = 255)
    private String referenceMandataire;

    @Column(nullable = false, length = 255)
    private String nomDossier;

    @Column(nullable = false, length = 255)
    private String signedPlace;

    @ManyToOne
    @JoinColumn(name = "id_type_formalite", nullable = false)
    TypeFormalite typeFormalite;

    @Column(nullable = false, length = 255)
    private String observationSignature;

    @ManyToOne
    @JoinColumn(name = "id_diffusion_insee", nullable = false)
    DiffusionINSEE diffusionINSEE;

    @Column(nullable = false)
    private Boolean indicateurEntreeSortieRegistre;

    @ManyToOne
    @JoinColumn(name = "id_type_personne", nullable = false)
    TypePersonne typePersonne;

    @Column(nullable = false)
    private Boolean inscriptionOffice;

    @Column(nullable = false, length = 255)
    private String inscriptionOfficePartnerCenter;

    @Column(nullable = false)
    private Boolean hasRnippBeenCalled;

    @Column(nullable = false)
    private Boolean indicateurNouvelleEntreprise;

    @Column(nullable = false)
    private Boolean optionEIRL;

    @Column(nullable = false)
    private Boolean optionME;

    @ManyToOne
    @JoinColumn(name = "id_forme_juridique", nullable = false)
    FormeJuridique formeJuridique;

    @Column(nullable = false)
    private Integer optionJqpaNumber;

    @Column(nullable = false)
    private Boolean regularisation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFormalityDraftId() {
        return formalityDraftId;
    }

    public void setFormalityDraftId(Integer formalityDraftId) {
        this.formalityDraftId = formalityDraftId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getReferenceMandataire() {
        return referenceMandataire;
    }

    public void setReferenceMandataire(String referenceMandataire) {
        this.referenceMandataire = referenceMandataire;
    }

    public String getNomDossier() {
        return nomDossier;
    }

    public void setNomDossier(String nomDossier) {
        this.nomDossier = nomDossier;
    }

    public String getSignedPlace() {
        return signedPlace;
    }

    public void setSignedPlace(String signedPlace) {
        this.signedPlace = signedPlace;
    }

    public TypeFormalite getTypeFormalite() {
        return typeFormalite;
    }

    public void setTypeFormalite(TypeFormalite typeFormalite) {
        this.typeFormalite = typeFormalite;
    }

    public String getObservationSignature() {
        return observationSignature;
    }

    public void setObservationSignature(String observationSignature) {
        this.observationSignature = observationSignature;
    }

    public DiffusionINSEE getDiffusionINSEE() {
        return diffusionINSEE;
    }

    public void setDiffusionINSEE(DiffusionINSEE diffusionINSEE) {
        this.diffusionINSEE = diffusionINSEE;
    }

    public Boolean getIndicateurEntreeSortieRegistre() {
        return indicateurEntreeSortieRegistre;
    }

    public void setIndicateurEntreeSortieRegistre(Boolean indicateurEntreeSortieRegistre) {
        this.indicateurEntreeSortieRegistre = indicateurEntreeSortieRegistre;
    }

    public TypePersonne getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(TypePersonne typePersonne) {
        this.typePersonne = typePersonne;
    }

    public Boolean getInscriptionOffice() {
        return inscriptionOffice;
    }

    public void setInscriptionOffice(Boolean inscriptionOffice) {
        this.inscriptionOffice = inscriptionOffice;
    }

    public String getInscriptionOfficePartnerCenter() {
        return inscriptionOfficePartnerCenter;
    }

    public void setInscriptionOfficePartnerCenter(String inscriptionOfficePartnerCenter) {
        this.inscriptionOfficePartnerCenter = inscriptionOfficePartnerCenter;
    }

    public Boolean getHasRnippBeenCalled() {
        return hasRnippBeenCalled;
    }

    public void setHasRnippBeenCalled(Boolean hasRnippBeenCalled) {
        this.hasRnippBeenCalled = hasRnippBeenCalled;
    }

    public Boolean getIndicateurNouvelleEntreprise() {
        return indicateurNouvelleEntreprise;
    }

    public void setIndicateurNouvelleEntreprise(Boolean indicateurNouvelleEntreprise) {
        this.indicateurNouvelleEntreprise = indicateurNouvelleEntreprise;
    }

    public Boolean getOptionEIRL() {
        return optionEIRL;
    }

    public void setOptionEIRL(Boolean optionEIRL) {
        this.optionEIRL = optionEIRL;
    }

    public Boolean getOptionME() {
        return optionME;
    }

    public void setOptionME(Boolean optionME) {
        this.optionME = optionME;
    }

    public FormeJuridique getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(FormeJuridique formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public Integer getOptionJqpaNumber() {
        return optionJqpaNumber;
    }

    public void setOptionJqpaNumber(Integer optionJqpaNumber) {
        this.optionJqpaNumber = optionJqpaNumber;
    }

    public Boolean getRegularisation() {
        return regularisation;
    }

    public void setRegularisation(Boolean regularisation) {
        this.regularisation = regularisation;
    }

}
