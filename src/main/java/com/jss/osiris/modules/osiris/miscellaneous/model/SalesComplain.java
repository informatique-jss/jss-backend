package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales_complain")
public class SalesComplain implements Serializable, IId {
  @Id
  @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_problem")
  private SalesComplainProblem salesProblem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_cause")
  private SalesComplainCause salesCause;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_origin")
  private SalesComplainOrigin salesOrigin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_affaire")
  private Affaire affaire;

  @Column(nullable = false)
  private Integer idTiers;

  @Column(nullable = false)
  @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
  private LocalDateTime complaintDate;

  @Column(nullable = false)
  private String responsableName;

  @Column(nullable = false)
  private String observations;

  @Column(nullable = false)
  private String customerOrderNumber;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public SalesComplainProblem getSalesProblem() {
    return salesProblem;
  }

  public void setSalesProblem(SalesComplainProblem salesProblem) {
    this.salesProblem = salesProblem;
  }

  public SalesComplainCause getSalesCause() {
    return salesCause;
  }

  public void setSalesCause(SalesComplainCause salesCause) {
    this.salesCause = salesCause;
  }

  public SalesComplainOrigin getSalesOrigin() {
    return salesOrigin;
  }

  public void setSalesOrigin(SalesComplainOrigin salesOrigin) {
    this.salesOrigin = salesOrigin;
  }

  public Affaire getAffaire() {
    return affaire;
  }

  public void setAffaire(Affaire affaire) {
    this.affaire = affaire;
  }

  public Integer getIdTiers() {
    return idTiers;
  }

  public void setIdTiers(Integer idTiers) {
    this.idTiers = idTiers;
  }

  public LocalDateTime getComplaintDate() {
    return complaintDate;
  }

  public void setComplaintDate(LocalDateTime complaintDate) {
    this.complaintDate = complaintDate;
  }

  public String getResponsableName() {
    return responsableName;
  }

  public void setResponsableName(String responsableName) {
    this.responsableName = responsableName;
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

  public String getCustomerOrderNumber() {
    return customerOrderNumber;
  }

  public void setCustomerOrderNumber(String customerOrderNumber) {
    this.customerOrderNumber = customerOrderNumber;
  }

}
