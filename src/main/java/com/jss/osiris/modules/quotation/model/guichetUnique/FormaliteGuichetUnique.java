package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DiffusionINSEE;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Status;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;

@Entity
@JsonIgnoreProperties
public class FormaliteGuichetUnique implements IId {

    @Id
    private Integer id;

    private String liasseNumber;

    private Integer formalityDraftId;

    @Column(length = 255)
    private String companyName;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_content")
    Content content;

    private String referenceMandataire;

    @Column(length = 255)
    private String nomDossier;

    @Column(length = 255)
    private String signedPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_formalite")
    TypeFormalite typeFormalite;

    @Column(columnDefinition = "TEXT")
    private String observationSignature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_diffusion_insee")
    DiffusionINSEE diffusionINSEE;

    private Boolean indicateurEntreeSortieRegistre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_personne")
    TypePersonne typePersonne;

    private Boolean inscriptionOffice;

    private String inscriptionOfficePartnerCenter;

    private Boolean hasRnippBeenCalled;

    private Boolean indicateurNouvelleEntreprise;

    private Boolean optionEIRL;

    private Boolean optionME;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forme_juridique")
    FormeJuridique formeJuridique;

    private Integer optionJqpaNumber;

    private Boolean regularisation;

    @OneToMany(mappedBy = "formaliteGuichetUnique", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "formaliteGuichetUnique" }, allowSetters = true)
    private List<Cart> carts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite")
    @JsonIgnoreProperties(value = { "formalitesGuichetUnique" })
    private Formalite formalite;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

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

    public String getReferenceMandataire() {
        return referenceMandataire;
    }

    public void setReferenceMandataire(String referenceMandataire) {
        this.referenceMandataire = referenceMandataire;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public String getLiasseNumber() {
        return liasseNumber;
    }

    public void setLiasseNumber(String liasseNumber) {
        this.liasseNumber = liasseNumber;
    }

    public Formalite getFormalite() {
        return formalite;
    }

    public void setFormalite(Formalite formalite) {
        this.formalite = formalite;
    }
}