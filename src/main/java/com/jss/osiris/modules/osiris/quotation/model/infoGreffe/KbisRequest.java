package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.time.LocalDate;

import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_kbis_request_siren", columnList = "siren") })
public class KbisRequest {

    @Id
    @SequenceGenerator(name = "kbis_request_sequence", sequenceName = "kbis_request_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kbis_request_sequence")
    private Integer id;

    private String siret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee_initiator")
    private Employee employeeInitiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_provision")
    private Provision provision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_attachment")
    private Attachment attachment;

    private String orderId;

    @Column(length = 1000)
    private String urlTelechargement;

    private LocalDate dateOrder;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployeeInitiator() {
        return employeeInitiator;
    }

    public void setEmployeeInitiator(Employee employeeInitiator) {
        this.employeeInitiator = employeeInitiator;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String documentId) {
        this.orderId = documentId;
    }

    public String getUrlTelechargement() {
        return urlTelechargement;
    }

    public void setUrlTelechargement(String urlTelechargement) {
        this.urlTelechargement = urlTelechargement;
    }

    public Provision getProvision() {
        return provision;
    }

    public void setProvision(Provision provision) {
        this.provision = provision;
    }

    public LocalDate getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(LocalDate dateOrder) {
        this.dateOrder = dateOrder;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

}
