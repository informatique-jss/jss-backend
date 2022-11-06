package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeExercice;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureCessation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SuccursaleOuFiliale;

@Entity
public class Content implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String evenementCessation;

    @ManyToOne
    @JoinColumn(name = "id_nature_cessation", nullable = false)
    NatureCessation natureCessation;

    @ManyToOne
    @JoinColumn(name = "id_succursale_ou_filiale", nullable = false)
    SuccursaleOuFiliale succursaleOuFiliale;

    @ManyToOne
    @JoinColumn(name = "id_forme_exercice_activite_principale", nullable = false)
    FormeExercice formeExerciceActivitePrincipale;

    @Column(nullable = false)
    private Boolean indicateurPoursuiteCessation;

    @Column(nullable = false, length = 255)
    private String tvaIntraCommunautaire;

    @ManyToOne
    @JoinColumn(name = "id_nature_creation", nullable = false)
    NatureCreation natureCreation;

    @ManyToOne
    @JoinColumn(name = "id_personne_physique", nullable = false)
    PersonnePhysique personnePhysique;

    @ManyToOne
    @JoinColumn(name = "id_personne_morale", nullable = false)
    PersonneMorale personneMorale;

    @ManyToOne
    @JoinColumn(name = "id_exploitation", nullable = false)
    Exploitation exploitation;

    @ManyToOne
    @JoinColumn(name = "id_declarant", nullable = false)
    Declarant declarant;

    @OneToMany(mappedBy = "content")
    @JsonIgnoreProperties(value = { "content" }, allowSetters = true)
    List<PiecesJointe> piecesJointes;

    @Column(nullable = false)
    private Boolean indicateurActive;

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

    public FormeExercice getFormeExerciceActivitePrincipale() {
        return formeExerciceActivitePrincipale;
    }

    public void setFormeExerciceActivitePrincipale(FormeExercice formeExerciceActivitePrincipale) {
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

}
