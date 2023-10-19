package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = {
        @Index(name = "idx_mineur_volet_social", columnList = "id_volet_social") })
public class Mineur implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_volet_social")
    @JsonIgnoreProperties(value = { "mineur" }, allowSetters = true)
    VoletSocial voletSocial;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public VoletSocial getVoletSocial() {
        return voletSocial;
    }

    public void setVoletSocial(VoletSocial voletSocial) {
        this.voletSocial = voletSocial;
    }

}
