package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;

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
@Table(indexes = { @Index(name = "employee_index", columnList = "id_employee") })
public class Notification implements Serializable, IId {

  public static String PERSONNAL = "PERSONNAL";

  public static String PROVISION_ADD_ATTACHMENT = "PROVISION_ADD_ATTACHMENT";
  public static String SERVICE_ADD_ATTACHMENT = "SERVICE_ADD_ATTACHMENT";
  public static String ORDER_ADD_ATTACHMENT = "ORDER_ADD_ATTACHMENT";
  public static String PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED = "PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED";
  public static String PROVISION_GUICHET_UNIQUE_STATUS_SIGNED = "PROVISION_GUICHET_UNIQUE_STATUS_SIGNED";

  @Id
  @SequenceGenerator(name = "notification_sequence", sequenceName = "notification_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_sequence")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee")
  private Employee employee;

  private Boolean isRead;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee_created_by")
  private Employee createdBy;

  @Column(nullable = false)
  private LocalDateTime createdDateTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee_updated_by")
  private Employee updatedBy;

  @Column(nullable = false)
  private LocalDateTime updatedDateTime;

  @Column(nullable = false)
  private String notificationType;

  @Column(length = 2000)
  private String detail1;

  private String summary;

  @Column(nullable = false)
  private Boolean showPopup;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_service")
  private Service service;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_provision")
  private Provision provision;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_customer_order")
  private CustomerOrder customerOrder;

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

  public static String getPERSONNAL() {
    return PERSONNAL;
  }

  public static void setPERSONNAL(String pERSONNAL) {
    PERSONNAL = pERSONNAL;
  }

  public static String getPROVISION_ADD_ATTACHMENT() {
    return PROVISION_ADD_ATTACHMENT;
  }

  public static void setPROVISION_ADD_ATTACHMENT(String pROVISION_ADD_ATTACHMENT) {
    PROVISION_ADD_ATTACHMENT = pROVISION_ADD_ATTACHMENT;
  }

  public static String getSERVICE_ADD_ATTACHMENT() {
    return SERVICE_ADD_ATTACHMENT;
  }

  public static void setSERVICE_ADD_ATTACHMENT(String sERVICE_ADD_ATTACHMENT) {
    SERVICE_ADD_ATTACHMENT = sERVICE_ADD_ATTACHMENT;
  }

  public static String getORDER_ADD_ATTACHMENT() {
    return ORDER_ADD_ATTACHMENT;
  }

  public static void setORDER_ADD_ATTACHMENT(String oRDER_ADD_ATTACHMENT) {
    ORDER_ADD_ATTACHMENT = oRDER_ADD_ATTACHMENT;
  }

  public static String getPROVISION_GUICHET_UNIQUE_STATUS_MODIFIED() {
    return PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED;
  }

  public static void setPROVISION_GUICHET_UNIQUE_STATUS_MODIFIED(String pROVISION_GUICHET_UNIQUE_STATUS_MODIFIED) {
    PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED = pROVISION_GUICHET_UNIQUE_STATUS_MODIFIED;
  }

  public static String getPROVISION_GUICHET_UNIQUE_STATUS_SIGNED() {
    return PROVISION_GUICHET_UNIQUE_STATUS_SIGNED;
  }

  public static void setPROVISION_GUICHET_UNIQUE_STATUS_SIGNED(String pROVISION_GUICHET_UNIQUE_STATUS_SIGNED) {
    PROVISION_GUICHET_UNIQUE_STATUS_SIGNED = pROVISION_GUICHET_UNIQUE_STATUS_SIGNED;
  }

  public String getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(String notificationType) {
    this.notificationType = notificationType;
  }

  public String getDetail1() {
    return detail1;
  }

  public void setDetail1(String detail1) {
    this.detail1 = detail1;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public Boolean getShowPopup() {
    return showPopup;
  }

  public void setShowPopup(Boolean showPopup) {
    this.showPopup = showPopup;
  }

  public Employee getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(Employee updatedBy) {
    this.updatedBy = updatedBy;
  }

  public LocalDateTime getUpdatedDateTime() {
    return updatedDateTime;
  }

  public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
    this.updatedDateTime = updatedDateTime;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }

  public Provision getProvision() {
    return provision;
  }

  public void setProvision(Provision provision) {
    this.provision = provision;
  }

  public CustomerOrder getCustomerOrder() {
    return customerOrder;
  }

  public void setCustomerOrder(CustomerOrder customerOrder) {
    this.customerOrder = customerOrder;
  }

}
