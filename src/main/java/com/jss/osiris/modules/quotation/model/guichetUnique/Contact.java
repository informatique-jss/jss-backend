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
public class Contact implements Serializable, IId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String roleDonneesContact;

    @Column(nullable = false, length = 255)
    private String mail;

    @Column(nullable = false, length = 255)
    private String telephone;

    @Column(nullable = false, length = 255)
    private String telecopie;

    @Column(nullable = false, length = 255)
    private String optionRgpd;

    @Column(nullable = false)
    private LocalDate dateEffet;

    @Column(nullable = false, length = 255)
    private String phoneCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleDonneesContact() {
        return roleDonneesContact;
    }

    public void setRoleDonneesContact(String roleDonneesContact) {
        this.roleDonneesContact = roleDonneesContact;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelecopie() {
        return telecopie;
    }

    public void setTelecopie(String telecopie) {
        this.telecopie = telecopie;
    }

    public String getOptionRgpd() {
        return optionRgpd;
    }

    public void setOptionRgpd(String optionRgpd) {
        this.optionRgpd = optionRgpd;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

}
