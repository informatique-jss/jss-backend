package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

@Entity
public class FormaliteStatusHistoryItem {
    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_guichet_unique")
    @JsonIgnoreProperties(value = { "formaliteStatusHistoryItems" }, allowSetters = true)
    private FormaliteGuichetUnique formaliteGuichetUnique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_status")
    private Status sourceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status")
    private Status status;

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

    public Status getSourceStatus() {
        return sourceStatus;
    }

    public void setSourceStatus(Status sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
