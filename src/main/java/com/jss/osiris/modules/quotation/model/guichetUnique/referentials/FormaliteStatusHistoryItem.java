package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

@Entity
@Table(indexes = {
        @Index(name = "idx_status_history_formalite", columnList = "id_formalite_guichet_unique") })
public class FormaliteStatusHistoryItem {
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_guichet_unique")
    @JsonIgnoreProperties(value = { "formaliteStatusHistoryItems" }, allowSetters = true)
    private FormaliteGuichetUnique formaliteGuichetUnique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_status")
    private FormaliteGuichetUniqueStatus sourceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status")
    private FormaliteGuichetUniqueStatus status;

    private String created;

    // TODO : ask inpi for format ... "partnerCenter": "/api/partner_centers/103",
    // @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
    // CascadeType.MERGE })
    // @JoinColumn(name = "id_partenaire")
    // private Partenaire partner;

    // TODO : ask inpi for format ... "partnerCenter": "/api/partner_centers/103",
    // @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
    // CascadeType.DETACH, CascadeType.REFRESH })
    // @JoinColumn(name = "id_partner_center")
    // private PartnerCenter partnerCenter;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FormaliteGuichetUnique getFormaliteGuichetUnique() {
        return formaliteGuichetUnique;
    }

    public void setFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique) {
        this.formaliteGuichetUnique = formaliteGuichetUnique;
    }

    public FormaliteGuichetUniqueStatus getSourceStatus() {
        return sourceStatus;
    }

    public void setSourceStatus(FormaliteGuichetUniqueStatus sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public FormaliteGuichetUniqueStatus getStatus() {
        return status;
    }

    public void setStatus(FormaliteGuichetUniqueStatus status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

}
