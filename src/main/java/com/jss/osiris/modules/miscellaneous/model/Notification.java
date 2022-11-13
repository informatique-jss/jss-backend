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

  public static String QUOTATION_CREATE = "QUOTATION_CREATE";
  public static String QUOTATION_SENT = "QUOTATION_SENT";
  public static String QUOTATION_VALIDATED_BY_CUSOMER = "QUOTATION_VALIDATED_BY_CUSOMER";
  public static String QUOTATION_REFUSED_BY_CUSOMER = "QUOTATION_REFUSED_BY_CUSOMER";
  public static String QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY = "QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY";

  public static String CUSTOMER_ORDER_CREATE = "CUSTOMER_ORDER_CREATE";
  public static String CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY = "CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY";
  public static String CUSTOMER_ORDER_BEING_PROCESSED = "CUSTOMER_ORDER_BEING_PROCESSED";
  public static String CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN = "CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN";
  public static String CUSTOMER_ORDER_TO_BE_BILLED = "CUSTOMER_ORDER_TO_BE_BILLED";

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

  private String notificationType;

  private String detail1;

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

  public static String getQUOTATION_CREATE() {
    return QUOTATION_CREATE;
  }

  public static void setQUOTATION_CREATE(String qUOTATION_CREATE) {
    QUOTATION_CREATE = qUOTATION_CREATE;
  }

  public String getDetail1() {
    return detail1;
  }

  public void setDetail1(String detail1) {
    this.detail1 = detail1;
  }

  public String getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(String notificationType) {
    this.notificationType = notificationType;
  }
}
