package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureCessation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SuccursaleOuFiliale;

@Entity
public class Content implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_content_sequence", sequenceName = "guichet_unique_content_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_content_sequence")
    private Integer id;

    @Column(length = 255)
    private String evenementCessation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nature_cessation")
    NatureCessation natureCessation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_succursale_ou_filiale")
    SuccursaleOuFiliale succursaleOuFiliale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forme_exercice_activite_principale")
    FormeExerciceActivitePrincipal formeExerciceActivitePrincipale;

    @Column
    private Boolean indicateurPoursuiteCessation;

    @Column(length = 255)
    private String tvaIntraCommunautaire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nature_creation")
    NatureCreation natureCreation;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne_physique")
    PersonnePhysique personnePhysique;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne_morale")
    PersonneMorale personneMorale;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exploitation")
    Exploitation exploitation;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_declarant")
    Declarant declarant;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "content" }, allowSetters = true)
    List<PiecesJointe> piecesJointes;

    private Boolean indicateurActive;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comptes_annuels")
    private ComptesAnnuels comptesAnnuels;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEvenementCessation() {
        return evenementCessation;
    }

    public void setEvenementCessation(String evenementCessation) {
        this.evenementCessation = evenementCessation;
    }

    public NatureCessation getNatureCessation() {
        return natureCessation;
    }

    public void setNatureCessation(NatureCessation natureCessation) {
        this.natureCessation = natureCessation;
    }

    public SuccursaleOuFiliale getSuccursaleOuFiliale() {
        return succursaleOuFiliale;
    }

    public void setSuccursaleOuFiliale(SuccursaleOuFiliale succursaleOuFiliale) {
        this.succursaleOuFiliale = succursaleOuFiliale;
    }

    public FormeExerciceActivitePrincipal getFormeExerciceActivitePrincipale() {
        return formeExerciceActivitePrincipale;
    }

    public void setFormeExerciceActivitePrincipale(FormeExerciceActivitePrincipal formeExerciceActivitePrincipale) {
        this.formeExerciceActivitePrincipale = formeExerciceActivitePrincipale;
    }

    public Boolean getIndicateurPoursuiteCessation() {
        return indicateurPoursuiteCessation;
    }

    public void setIndicateurPoursuiteCessation(Boolean indicateurPoursuiteCessation) {
        this.indicateurPoursuiteCessation = indicateurPoursuiteCessation;
    }

    public String getTvaIntraCommunautaire() {
        return tvaIntraCommunautaire;
    }

    public void setTvaIntraCommunautaire(String tvaIntraCommunautaire) {
        this.tvaIntraCommunautaire = tvaIntraCommunautaire;
    }

    public NatureCreation getNatureCreation() {
        return natureCreation;
    }

    public void setNatureCreation(NatureCreation natureCreation) {
        this.natureCreation = natureCreation;
    }

    public PersonnePhysique getPersonnePhysique() {
        return personnePhysique;
    }

    public void setPersonnePhysique(PersonnePhysique personnePhysique) {
        this.personnePhysique = personnePhysique;
    }

    public PersonneMorale getPersonneMorale() {
        return personneMorale;
    }

    public void setPersonneMorale(PersonneMorale personneMorale) {
        this.personneMorale = personneMorale;
    }

    public Exploitation getExploitation() {
        return exploitation;
    }

    public void setExploitation(Exploitation exploitation) {
        this.exploitation = exploitation;
    }

    public Declarant getDeclarant() {
        return declarant;
    }

    public void setDeclarant(Declarant declarant) {
        this.declarant = declarant;
    }

    public Boolean getIndicateurActive() {
        return indicateurActive;
    }

    public void setIndicateurActive(Boolean indicateurActive) {
        this.indicateurActive = indicateurActive;
    }

    public List<PiecesJointe> getPiecesJointes() {
        return piecesJointes;
    }

    public void setPiecesJointes(List<PiecesJointe> piecesJointes) {
        this.piecesJointes = piecesJointes;
    }

    public ComptesAnnuels getComptesAnnuels() {
        return comptesAnnuels;
    }

    public void setComptesAnnuels(ComptesAnnuels comptesAnnuels) {
        this.comptesAnnuels = comptesAnnuels;
    }

}
