package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Declarant implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lien_entreprise")
    LienEntreprise lienEntreprise;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_individu")
    Repreneur individu;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise_mandataire")
    EntrepriseMandataire entrepriseMandataire;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LienEntreprise getLienEntreprise() {
        return lienEntreprise;
    }

    public void setLienEntreprise(LienEntreprise lienEntreprise) {
        this.lienEntreprise = lienEntreprise;
    }

    public Repreneur getIndividu() {
        return individu;
    }

    public void setIndividu(Repreneur individu) {
        this.individu = individu;
    }

    public EntrepriseMandataire getEntrepriseMandataire() {
        return entrepriseMandataire;
    }

    public void setEntrepriseMandataire(EntrepriseMandataire entrepriseMandataire) {
        this.entrepriseMandataire = entrepriseMandataire;
    }

}
