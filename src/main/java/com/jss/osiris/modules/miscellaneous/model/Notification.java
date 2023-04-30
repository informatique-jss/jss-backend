package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.jss.osiris.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "employee_index", columnList = "id_employee") })
public class Notification implements Serializable, IId {

  public static String PERSONNAL = "PERSONNAL";

  public static String QUOTATION_CREATE = "QUOTATION_CREATE";
  public static String QUOTATION_SENT = "QUOTATION_SENT";
  public static String QUOTATION_VALIDATED_BY_CUSOMER = "QUOTATION_VALIDATED_BY_CUSOMER";
  public static String QUOTATION_REFUSED_BY_CUSOMER = "QUOTATION_REFUSED_BY_CUSOMER";
  public static String QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY = "QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY";

  public static String CUSTOMER_ORDER_CREATE = "CUSTOMER_ORDER_CREATE";
  public static String CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY = "CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY";
  public static String CUSTOMER_ORDER_BEING_PROCESSED = "CUSTOMER_ORDER_BEING_PROCESSED";
  public static String CUSTOMER_ORDER_BEING_PROCESSED_FROM_DEPOSIT = "CUSTOMER_ORDER_BEING_PROCESSED_FROM_DEPOSIT";
  public static String CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN = "CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN";
  public static String CUSTOMER_ORDER_TO_BE_BILLED = "CUSTOMER_ORDER_TO_BE_BILLED";

  public static String INVOICE_REMINDER_PAYMENT = "INVOICE_REMINDER_PAYMENT";

  public static String PROVISION_ADD_ATTACHMENT = "PROVISION_ADD_ATTACHMENT";
  public static String PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED = "PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED";

  @Id
  @SequenceGenerator(name = "notification_sequence", sequenceName = "notification_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_sequence")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee")
  private Employee employee;

  private Boolean isRead;

  private String entityType;

  private Integer entityId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee_created_by")
  private Employee createdBy;

  @Column(nullable = false)
  private LocalDateTime createdDateTime;

  @Column(nullable = false)
  private String notificationType;

  @Column(length = 2000)
  private String detail1;

  private String summary;

  @Column(nullable = false)
  private Boolean showPopup;

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

  public static String getPERSONNAL() {
    return PERSONNAL;
  }

  public static void setPERSONNAL(String pERSONNAL) {
    PERSONNAL = pERSONNAL;
  }

  public static String getQUOTATION_SENT() {
    return QUOTATION_SENT;
  }

  public static void setQUOTATION_SENT(String qUOTATION_SENT) {
    QUOTATION_SENT = qUOTATION_SENT;
  }

  public static String getQUOTATION_VALIDATED_BY_CUSOMER() {
    return QUOTATION_VALIDATED_BY_CUSOMER;
  }

  public static void setQUOTATION_VALIDATED_BY_CUSOMER(String qUOTATION_VALIDATED_BY_CUSOMER) {
    QUOTATION_VALIDATED_BY_CUSOMER = qUOTATION_VALIDATED_BY_CUSOMER;
  }

  public static String getQUOTATION_REFUSED_BY_CUSOMER() {
    return QUOTATION_REFUSED_BY_CUSOMER;
  }

  public static void setQUOTATION_REFUSED_BY_CUSOMER(String qUOTATION_REFUSED_BY_CUSOMER) {
    QUOTATION_REFUSED_BY_CUSOMER = qUOTATION_REFUSED_BY_CUSOMER;
  }

  public static String getQUOTATION_ASSO_AFFAIRE_ORDER_VERIFY() {
    return QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY;
  }

  public static void setQUOTATION_ASSO_AFFAIRE_ORDER_VERIFY(String qUOTATION_ASSO_AFFAIRE_ORDER_VERIFY) {
    QUOTATION_ASSO_AFFAIRE_ORDER_VERIFY = qUOTATION_ASSO_AFFAIRE_ORDER_VERIFY;
  }

  public static String getCUSTOMER_ORDER_CREATE() {
    return CUSTOMER_ORDER_CREATE;
  }

  public static void setCUSTOMER_ORDER_CREATE(String cUSTOMER_ORDER_CREATE) {
    CUSTOMER_ORDER_CREATE = cUSTOMER_ORDER_CREATE;
  }

  public static String getCUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY() {
    return CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY;
  }

  public static void setCUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY(String cUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY) {
    CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY = cUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_VERIFY;
  }

  public static String getCUSTOMER_ORDER_BEING_PROCESSED() {
    return CUSTOMER_ORDER_BEING_PROCESSED;
  }

  public static void setCUSTOMER_ORDER_BEING_PROCESSED(String cUSTOMER_ORDER_BEING_PROCESSED) {
    CUSTOMER_ORDER_BEING_PROCESSED = cUSTOMER_ORDER_BEING_PROCESSED;
  }

  public static String getCUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN() {
    return CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN;
  }

  public static void setCUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN(
      String cUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN) {
    CUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN = cUSTOMER_ORDER_ASSO_AFFAIRE_ORDER_TO_ASSIGN;
  }

  public static String getCUSTOMER_ORDER_TO_BE_BILLED() {
    return CUSTOMER_ORDER_TO_BE_BILLED;
  }

  public static void setCUSTOMER_ORDER_TO_BE_BILLED(String cUSTOMER_ORDER_TO_BE_BILLED) {
    CUSTOMER_ORDER_TO_BE_BILLED = cUSTOMER_ORDER_TO_BE_BILLED;
  }

  public Boolean getShowPopup() {
    return showPopup;
  }

  public void setShowPopup(Boolean showPopup) {
    this.showPopup = showPopup;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

}
