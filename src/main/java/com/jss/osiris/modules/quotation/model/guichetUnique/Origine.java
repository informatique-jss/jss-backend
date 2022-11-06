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
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeOrigine;

@Entity
public class Origine implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_type_origine", nullable = false)
    TypeOrigine typeOrigine;

    @Column(nullable = false, length = 255)
    private String autreOrigine;

    @ManyToOne
    @JoinColumn(name = "id_contrat", nullable = false)
    Contrat contrat;

    @ManyToOne
    @JoinColumn(name = "id_ancien_exploitant", nullable = false)
    AncienExploitant ancienExploitant;

    @ManyToOne
    @JoinColumn(name = "id_publication", nullable = false)
    Publication publication;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TypeOrigine getTypeOrigine() {
        return typeOrigine;
    }

    public void setTypeOrigine(TypeOrigine typeOrigine) {
        this.typeOrigine = typeOrigine;
    }

    public String getAutreOrigine() {
        return autreOrigine;
    }

    public void setAutreOrigine(String autreOrigine) {
        this.autreOrigine = autreOrigine;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public AncienExploitant getAncienExploitant() {
        return ancienExploitant;
    }

    public void setAncienExploitant(AncienExploitant ancienExploitant) {
        this.ancienExploitant = ancienExploitant;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

}
