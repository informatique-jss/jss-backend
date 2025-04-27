package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

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

  public static List<String> notificationTypes = Arrays.asList(PROVISION_ADD_ATTACHMENT, SERVICE_ADD_ATTACHMENT,
      ORDER_ADD_ATTACHMENT, PROVISION_GUICHET_UNIQUE_STATUS_MODIFIED, PROVISION_GUICHET_UNIQUE_STATUS_SIGNED,
      PERSONNAL);

  @Id
  @SequenceGenerator(name = "notification_sequence", sequenceName = "notification_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_sequence")
  @JsonView(JacksonViews.OsirisListView.class)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee")
  private Employee employee;

  @JsonView(JacksonViews.OsirisListView.class)
  private Boolean isRead;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee_created_by")
  @JsonView(JacksonViews.OsirisListView.class)
  private Employee createdBy;

  @Column(nullable = false)
  @JsonView(JacksonViews.OsirisListView.class)
  private LocalDateTime createdDateTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_employee_updated_by")
  @JsonView(JacksonViews.OsirisListView.class)
  private Employee updatedBy;

  @JsonView(JacksonViews.OsirisListView.class)
  private LocalDateTime updatedDateTime;

  @Column(nullable = false)
  @JsonView(JacksonViews.OsirisListView.class)
  private String notificationType;

  @Column(length = 2000)
  @JsonView(JacksonViews.OsirisListView.class)
  private String detail1;

  private String summary;

  private Boolean showPopup;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_service")
  @JsonView(JacksonViews.OsirisListView.class)
  private Service service;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_provision")
  @JsonView(JacksonViews.OsirisListView.class)
  private Provision provision;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_customer_order")
  @JsonView(JacksonViews.OsirisListView.class)
  private CustomerOrder customerOrder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_invoice")
  @JsonView(JacksonViews.OsirisListView.class)
  private Invoice invoice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_quotation")
  @JsonView(JacksonViews.OsirisListView.class)
  private Quotation quotation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_tiers")
  @JsonView(JacksonViews.OsirisListView.class)
  private Tiers tiers;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_responsable")
  @JsonView(JacksonViews.OsirisListView.class)
  private Responsable responsable;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_affaire")
  @JsonView(JacksonViews.OsirisListView.class)
  private Affaire affaire;

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

  public static List<String> getNotificationTypes() {
    return notificationTypes;
  }

  public static void setNotificationTypes(List<String> notificationTypes) {
    Notification.notificationTypes = notificationTypes;
  }

  public Invoice getInvoice() {
    return invoice;
  }

  public void setInvoice(Invoice invoice) {
    this.invoice = invoice;
  }

  public Quotation getQuotation() {
    return quotation;
  }

  public void setQuotation(Quotation quotation) {
    this.quotation = quotation;
  }

  public Tiers getTiers() {
    return tiers;
  }

  public void setTiers(Tiers tiers) {
    this.tiers = tiers;
  }

  public Responsable getResponsable() {
    return responsable;
  }

  public void setResponsable(Responsable responsable) {
    this.responsable = responsable;
  }

  public Affaire getAffaire() {
    return affaire;
  }

  public void setAffaire(Affaire affaire) {
    this.affaire = affaire;
  }

}
