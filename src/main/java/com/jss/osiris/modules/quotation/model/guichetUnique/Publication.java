package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Publication implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String rolePublication;

    @Column(nullable = false, length = 255)
    private String typePublication;

    @Column(nullable = false)
    private LocalDate datePublication;

    @Column(nullable = false, length = 255)
    private String journalPublication;

    @Column(nullable = false, length = 255)
    private String lieuPublication;

    @Column(nullable = false)
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
