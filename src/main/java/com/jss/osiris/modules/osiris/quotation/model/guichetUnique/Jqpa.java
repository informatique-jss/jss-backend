package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.CodeRolePersonneQualifiee;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OptionJQPA;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

public class Jqpa implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_option_jqpa")
    OptionJQPA optionJQPA;

    private Boolean indicateurActivitesMultiples;

    @Column(length = 255)
    private String descriptionActivite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_code_role_personne_qualifiee")
    CodeRolePersonneQualifiee codeRolePersonneQualifiee;

    @Column(length = 255)
    private String libelleRolePersonneQualifiee;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personne_qualifiee")
    PersonneQualifiee personneQualifiee;

    @Column(length = 255)
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
