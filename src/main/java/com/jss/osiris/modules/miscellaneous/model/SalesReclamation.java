package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.modules.quotation.model.Affaire;

@Entity
@Table(name = "sales_reclamation")
public class SalesReclamation implements Serializable, IId {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_problem")
  private SalesReclamationProblem salesProblem;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_cause")
  private SalesReclamationProblem salesCause;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_origin")
  private SalesReclamationOrigin salesOrigin;

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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public SalesReclamationProblem getSalesProblem() {
    return salesProblem;
  }

  public void setSalesProblem(SalesReclamationProblem salesProblem) {
    this.salesProblem = salesProblem;
  }

  public SalesReclamationProblem getSalesCause() {
    return salesCause;
  }

  public void setSalesCause(SalesReclamationProblem salesCause) {
    this.salesCause = salesCause;
  }

  public SalesReclamationOrigin getSalesOrigin() {
    return salesOrigin;
  }

  public void setSalesOrigin(SalesReclamationOrigin salesOrigin) {
    this.salesOrigin = salesOrigin;
  }

  public Affaire getAffaire() {
    return affaire;
  }

  public void setAffaire(Affaire affaire) {
    this.affaire = affaire;
  }

  public LocalDateTime getComplaintDate() {
    return complaintDate;
  }

  public void setComplaintDate(LocalDateTime complaintDate) {
    this.complaintDate = complaintDate;
  }

  public Integer getIdTiers() {
    return idTiers;
  }

  public void setIdTiers(Integer idTiers) {
    this.idTiers = idTiers;
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
}
