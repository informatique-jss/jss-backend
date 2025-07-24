package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
@Cacheable(false)
@DoNotAudit
public class Publication implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "guichet_unique_sequence", sequenceName = "guichet_unique_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_sequence")
    private Integer id;

    @Column(length = 255)
    private String rolePublication;

    @Column(length = 255)
    private String typePublication;

    private LocalDate datePublication;

    @Column(length = 255)
    private String journalPublication;

    @Column(length = 255)
    private String lieuPublication;

    private LocalDate dateEffetPublication;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRolePublication() {
        return rolePublication;
    }

    public void setRolePublication(String rolePublication) {
        this.rolePublication = rolePublication;
    }

    public String getTypePublication() {
        return typePublication;
    }

    public void setTypePublication(String typePublication) {
        this.typePublication = typePublication;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    public String getJournalPublication() {
        return journalPublication;
    }

    public void setJournalPublication(String journalPublication) {
        this.journalPublication = journalPublication;
    }

    public String getLieuPublication() {
        return lieuPublication;
    }

    public void setLieuPublication(String lieuPublication) {
        this.lieuPublication = lieuPublication;
    }

    public LocalDate getDateEffetPublication() {
        return dateEffetPublication;
    }

    public void setDateEffetPublication(LocalDate dateEffetPublication) {
        this.dateEffetPublication = dateEffetPublication;
    }

}
