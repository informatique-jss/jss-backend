package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Declarant implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
