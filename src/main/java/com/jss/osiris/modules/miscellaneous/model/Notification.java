package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jss.osiris.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "employee_index", columnList = "id_employee") })
public class Notification implements Serializable, IId {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "id_employee")
  private Employee employee;

  private Boolean isRead;

  private String entityType;

  private Integer entityId;

  @ManyToOne
  @JoinColumn(name = "id_employee_created_by")
  private Employee createdBy;

  private LocalDateTime createdDateTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public Boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }

  public Employee getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Employee createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(LocalDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }
}
