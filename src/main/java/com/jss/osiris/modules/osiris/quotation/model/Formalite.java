package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.FormaliteInfogreffe;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@JsonIgnoreProperties
@Table(indexes = { @Index(name = "idx_formalite_status", columnList = "id_formalite_status"),
        @Index(name = "idx_formalite_id_waited_competent_authority", columnList = "id_waited_competent_authority"),
})
public class Formalite implements IId, Serializable {

    @Id
    @SequenceGenerator(name = "formalite_sequence", sequenceName = "formalite_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "formalite_sequence")
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_status")
    @IndexedField
    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private FormaliteStatus formaliteStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_waited_competent_authority")
    @JsonIgnoreProperties(value = { "attachments", "departments", "cities", "regions" }, allowSetters = true)
    @JsonView({ JacksonViews.OsirisDetailedView.class })
    private CompetentAuthority waitedCompetentAuthority;

    @OneToMany(mappedBy = "formalite")
    @JsonIgnoreProperties(value = { "content" })
    @IndexedField
    @JsonView({ JacksonViews.OsirisDetailedView.class })
    private List<FormaliteGuichetUnique> formalitesGuichetUnique;

    @OneToMany(mappedBy = "formalite")
    @IndexedField
    @JsonView({ JacksonViews.OsirisDetailedView.class })
    private List<FormaliteInfogreffe> formalitesInfogreffe;

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

    public List<FormaliteInfogreffe> getFormalitesInfogreffe() {
        return formalitesInfogreffe;
    }

    public void setFormalitesInfogreffe(List<FormaliteInfogreffe> formalitesInfogreffe) {
        this.formalitesInfogreffe = formalitesInfogreffe;
    }

    public ActeDeposit getActeDeposit() {
        return acteDeposit;
    }

    public void setActeDeposit(ActeDeposit acteDeposit) {
        this.acteDeposit = acteDeposit;
    }

}
