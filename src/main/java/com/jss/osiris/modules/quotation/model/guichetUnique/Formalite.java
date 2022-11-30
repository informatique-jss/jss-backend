package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DiffusionINSEE;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;

@Entity
@JsonIgnoreProperties
public class Formalite implements IId, IAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy = "formalite")
    @JsonIgnoreProperties(value = { "formalite" }, allowSetters = true)
    private List<Attachment> attachments;

    private Integer formalityDraftId;

    @Column(length = 255)
    private String companyName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_content", nullable = false)
    Content content;

    private String referenceMandataire;

    @Column(nullable = false, length = 255)
    private String nomDossier;

    @Column(nullable = false, length = 255)
    private String signedPlace;

    @ManyToOne
    @JoinColumn(name = "id_type_formalite", nullable = false)
    TypeFormalite typeFormalite;

    @ManyToOne
    @JoinColumn(name = "id_formalite_status")
    private FormaliteStatus formaliteStatus;

    @Column(columnDefinition = "TEXT")
    private String observationSignature;

    @ManyToOne
    @JoinColumn(name = "id_diffusion_insee", nullable = false)
    DiffusionINSEE diffusionINSEE;

    @Column(nullable = false)
    private Boolean indicateurEntreeSortieRegistre;

    @ManyToOne
    @JoinColumn(name = "id_type_personne", nullable = false)
    TypePersonne typePersonne;

    private Boolean inscriptionOffice;

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
    @JoinColumn(name = "id_forme_juridique")
    FormeJuridique formeJuridique;

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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public FormaliteStatus getFormaliteStatus() {
        return formaliteStatus;
    }

    public void setFormaliteStatus(FormaliteStatus formaliteStatus) {
        this.formaliteStatus = formaliteStatus;
    }

    public String getReferenceMandataire() {
        return referenceMandataire;
    }

    public void setReferenceMandataire(String referenceMandataire) {
        this.referenceMandataire = referenceMandataire;
    }

}
