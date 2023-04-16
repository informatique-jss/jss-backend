package com.jss.osiris.modules.quotation.model.guichetUnique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;

@Entity
@JsonIgnoreProperties
public class Formalite implements IId {

    @Id
    @SequenceGenerator(name = "formalite_sequence", sequenceName = "formalite_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "formalite_sequence")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_simple_provision_status")
    @IndexedField
    private FormaliteStatus formaliteStatus;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne
    @JoinColumn(name = "id_waited_competent_authority")
    @JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
    private CompetentAuthority waitedCompetentAuthority;

    @ManyToOne
    @JoinColumn(name = "id_competent_authority_service_provider")
    @JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
    private CompetentAuthority competentAuthorityServiceProvider;

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
}
