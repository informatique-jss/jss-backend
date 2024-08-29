package com.jss.osiris.modules.quotation.model.infoGreffe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.quotation.model.Formalite;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class FormaliteInfogreffe {

    public static String INFOGREFFE_STATUS_RECEIVED = "RECU_PAR_LE_GRF";
    public static String INFOGREFFE_STATUS_STRICT_REJECT = "REJETE_DEF_GRF";
    public static String INFOGREFFE_STATUS_REJECT = "REJETE_GRF";
    public static String INFOGREFFE_STATUS_VALIDATED = "VALIDE";

    @Id
    @JsonProperty("id")
    private String id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_identifiant_formalite")
    private IdentifiantFormalite identifiantFormalite;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    private Entreprise entreprise;
    private String personnePhysique;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_greffe_destinataire")
    private GreffeInfogreffe greffeDestinataire;

    private String referenceTechnique;
    private String referenceClient;
    private String numeroLiasse;
    private String typeFormalite;

    @OneToMany(mappedBy = "formaliteInfogreffe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "formaliteInfogreffe" }, allowSetters = true)
    private List<EvenementInfogreffe> evenements;

    private String evtCommentaire;
    private Boolean suppressionPossible;
    private Boolean reprisePossible;
    private Boolean detailPossible;
    private String urlReprise;
    private Boolean cloture;
    private String infosSpecifiquesFormaliteDpa;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_montant_formalite")
    private MontantFormalite montantFormalite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite")
    @JsonIgnoreProperties(value = { "formalitesInfogreffe", "formalitesGuichetUnique" })
    private Formalite formalite;

    public IdentifiantFormalite getIdentifiantFormalite() {
        return identifiantFormalite;
    }

    public void setIdentifiantFormalite(IdentifiantFormalite identifiantFormalite) {
        this.identifiantFormalite = identifiantFormalite;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public String getPersonnePhysique() {
        return personnePhysique;
    }

    public void setPersonnePhysique(String personnePhysique) {
        this.personnePhysique = personnePhysique;
    }

    public GreffeInfogreffe getGreffeDestinataire() {
        return greffeDestinataire;
    }

    public void setGreffeDestinataire(GreffeInfogreffe greffeDestinataire) {
        this.greffeDestinataire = greffeDestinataire;
    }

    public String getReferenceTechnique() {
        return referenceTechnique;
    }

    public void setReferenceTechnique(String referenceTechnique) {
        this.referenceTechnique = referenceTechnique;
    }

    public String getReferenceClient() {
        return referenceClient;
    }

    public void setReferenceClient(String referenceClient) {
        this.referenceClient = referenceClient;
    }

    public String getNumeroLiasse() {
        return numeroLiasse;
    }

    public void setNumeroLiasse(String numeroLiasse) {
        this.numeroLiasse = numeroLiasse;
    }

    public String getTypeFormalite() {
        return typeFormalite;
    }

    public void setTypeFormalite(String typeFormalite) {
        this.typeFormalite = typeFormalite;
    }

    public List<EvenementInfogreffe> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<EvenementInfogreffe> evenements) {
        this.evenements = evenements;
    }

    public String getEvtCommentaire() {
        return evtCommentaire;
    }

    public void setEvtCommentaire(String evtCommentaire) {
        this.evtCommentaire = evtCommentaire;
    }

    public Boolean getSuppressionPossible() {
        return suppressionPossible;
    }

    public void setSuppressionPossible(Boolean suppressionPossible) {
        this.suppressionPossible = suppressionPossible;
    }

    public Boolean getReprisePossible() {
        return reprisePossible;
    }

    public void setReprisePossible(Boolean reprisePossible) {
        this.reprisePossible = reprisePossible;
    }

    public Boolean getDetailPossible() {
        return detailPossible;
    }

    public void setDetailPossible(Boolean detailPossible) {
        this.detailPossible = detailPossible;
    }

    public String getUrlReprise() {
        return urlReprise;
    }

    public void setUrlReprise(String urlReprise) {
        this.urlReprise = urlReprise;
    }

    public Boolean getCloture() {
        return cloture;
    }

    public void setCloture(Boolean cloture) {
        this.cloture = cloture;
    }

    public String getInfosSpecifiquesFormaliteDpa() {
        return infosSpecifiquesFormaliteDpa;
    }

    public void setInfosSpecifiquesFormaliteDpa(String infosSpecifiquesFormaliteDpa) {
        this.infosSpecifiquesFormaliteDpa = infosSpecifiquesFormaliteDpa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MontantFormalite getMontantFormalite() {
        return montantFormalite;
    }

    public void setMontantFormalite(MontantFormalite montantFormalite) {
        this.montantFormalite = montantFormalite;
    }

    public Formalite getFormalite() {
        return formalite;
    }

    public void setFormalite(Formalite formalite) {
        this.formalite = formalite;
    }
}
