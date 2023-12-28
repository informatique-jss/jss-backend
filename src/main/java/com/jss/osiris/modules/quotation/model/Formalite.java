package com.jss.osiris.modules.quotation.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

@Entity
@JsonIgnoreProperties
public class Formalite implements IId {

    @Id
    @SequenceGenerator(name = "formalite_sequence", sequenceName = "formalite_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "formalite_sequence")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_status")
    @IndexedField
    private FormaliteStatus formaliteStatus;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_waited_competent_authority")
    @JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
    private CompetentAuthority waitedCompetentAuthority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competent_authority_service_provider")
    @JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
    private CompetentAuthority competentAuthorityServiceProvider;

    @OneToMany(mappedBy = "formalite")
    @JsonIgnoreProperties(value = { "content" })
    private List<FormaliteGuichetUnique> formalitesGuichetUnique;

    @OneToMany(mappedBy = "formalite")
    @JsonIgnore
    private List<Provision> provision;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_acte_deposit")
    private ActeDeposit acteDeposit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public CompetentAuthority getWaitedCompetentAuthority() {
        return waitedCompetentAuthority;
    }

    public void setWaitedCompetentAuthority(CompetentAuthority waitedCompetentAuthority) {
        this.waitedCompetentAuthority = waitedCompetentAuthority;
    }

    public FormaliteStatus getFormaliteStatus() {
        return formaliteStatus;
    }

    public void setFormaliteStatus(FormaliteStatus formaliteStatus) {
        this.formaliteStatus = formaliteStatus;
    }

    public CompetentAuthority getCompetentAuthorityServiceProvider() {
        return competentAuthorityServiceProvider;
    }

    public void setCompetentAuthorityServiceProvider(CompetentAuthority competentAuthorityServiceProvider) {
        this.competentAuthorityServiceProvider = competentAuthorityServiceProvider;
    }

    public List<Provision> getProvision() {
        return provision;
    }

    public void setProvision(List<Provision> provision) {
        this.provision = provision;
    }

    public List<FormaliteGuichetUnique> getFormalitesGuichetUnique() {
        return formalitesGuichetUnique;
    }

    public void setFormalitesGuichetUnique(List<FormaliteGuichetUnique> formalitesGuichetUnique) {
        this.formalitesGuichetUnique = formalitesGuichetUnique;
    }

    public ActeDeposit getActeDeposit() {
        return acteDeposit;
    }

    public void setActeDeposit(ActeDeposit acteDeposit) {
        this.acteDeposit = acteDeposit;
    }

}
