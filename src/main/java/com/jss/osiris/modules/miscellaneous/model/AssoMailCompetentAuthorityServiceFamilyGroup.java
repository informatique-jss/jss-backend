package com.jss.osiris.modules.miscellaneous.model;

import java.util.List;

import com.jss.osiris.modules.quotation.model.ServiceFamilyGroup;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class AssoMailCompetentAuthorityServiceFamilyGroup {

    @Id
    @SequenceGenerator(name = "asso_mail_competent_authority_service_family_group_sequence", sequenceName = "asso_mail_competent_authority_service_family_group_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_mail_competent_authority_service_family_group_sequence")
    private Integer id;

    @ManyToMany
    @JoinTable(name = "asso_mail_competent_authority_service_family_group_mail", joinColumns = @JoinColumn(name = "id_asso_mail_competent_authority_service_family_group"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
    private List<Mail> mails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_service_family_group")
    private ServiceFamilyGroup serviceFamilyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_competent_authority")
    private CompetentAuthority competentAuthority;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Mail> getMails() {
        return mails;
    }

    public void setMails(List<Mail> mails) {
        this.mails = mails;
    }

    public ServiceFamilyGroup getServiceFamilyGroup() {
        return serviceFamilyGroup;
    }

    public void setServiceFamilyGroup(ServiceFamilyGroup serviceFamilyGroup) {
        this.serviceFamilyGroup = serviceFamilyGroup;
    }

    public CompetentAuthority getCompetentAuthority() {
        return competentAuthority;
    }

    public void setCompetentAuthority(CompetentAuthority competentAuthority) {
        this.competentAuthority = competentAuthority;
    };

}
