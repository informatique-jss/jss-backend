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
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeRolePersonneQualifiee;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionJQPA;

@Entity
public class Jqpa implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_option_jqpa", nullable = false)
    OptionJQPA optionJQPA;

    @Column(nullable = false)
    private Boolean indicateurActivitesMultiples;

    @Column(nullable = false, length = 255)
    private String descriptionActivite;

    @ManyToOne
    @JoinColumn(name = "id_code_role_personne_qualifiee", nullable = false)
    CodeRolePersonneQualifiee codeRolePersonneQualifiee;

    @Column(nullable = false, length = 255)
    private String libelleRolePersonneQualifiee;

    @ManyToOne
    @JoinColumn(name = "id_personne_qualifiee", nullable = false)
    PersonneQualifiee personneQualifiee;

    @Column(nullable = false, length = 255)
    private String autreQualitePersonneQualifiee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OptionJQPA getOptionJQPA() {
        return optionJQPA;
    }

    public void setOptionJQPA(OptionJQPA optionJQPA) {
        this.optionJQPA = optionJQPA;
    }

    public Boolean getIndicateurActivitesMultiples() {
        return indicateurActivitesMultiples;
    }

    public void setIndicateurActivitesMultiples(Boolean indicateurActivitesMultiples) {
        this.indicateurActivitesMultiples = indicateurActivitesMultiples;
    }

    public String getDescriptionActivite() {
        return descriptionActivite;
    }

    public void setDescriptionActivite(String descriptionActivite) {
        this.descriptionActivite = descriptionActivite;
    }

    public CodeRolePersonneQualifiee getCodeRolePersonneQualifiee() {
        return codeRolePersonneQualifiee;
    }

    public void setCodeRolePersonneQualifiee(CodeRolePersonneQualifiee codeRolePersonneQualifiee) {
        this.codeRolePersonneQualifiee = codeRolePersonneQualifiee;
    }

    public String getLibelleRolePersonneQualifiee() {
        return libelleRolePersonneQualifiee;
    }

    public void setLibelleRolePersonneQualifiee(String libelleRolePersonneQualifiee) {
        this.libelleRolePersonneQualifiee = libelleRolePersonneQualifiee;
    }

    public PersonneQualifiee getPersonneQualifiee() {
        return personneQualifiee;
    }

    public void setPersonneQualifiee(PersonneQualifiee personneQualifiee) {
        this.personneQualifiee = personneQualifiee;
    }

    public String getAutreQualitePersonneQualifiee() {
        return autreQualitePersonneQualifiee;
    }

    public void setAutreQualitePersonneQualifiee(String autreQualitePersonneQualifiee) {
        this.autreQualitePersonneQualifiee = autreQualitePersonneQualifiee;
    }

}
