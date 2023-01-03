package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Entity
public class Deposit implements Serializable, IId {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false)
  private String label;

  @Column(nullable = false)
  private LocalDateTime depositDate;

  @Column(nullable = false)
  private Float depositAmount;

  @OneToMany(mappedBy = "deposit")
  @JsonIgnoreProperties(value = { "deposit" }, allowSetters = true)
  private List<AccountingRecord> accountingRecords;

  @ManyToOne
  @JoinColumn(name = "id_invoice")
  @JsonIgnoreProperties(value = { "deposits", "accountingRecords" }, allowSetters = true)
  private Invoice invoice;

  @ManyToOne
  @JoinColumn(name = "id_customer_order")
  @JsonIgnoreProperties(value = { "deposits" }, allowSetters = true)
  private CustomerOrder customerOrder;

  @ManyToOne
  @JoinColumn(name = "id_origin_payment")
  private Payment originPayment;

  private Boolean isCancelled;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Invoice getInvoice() {
    return invoice;
  }

  public void setInvoice(Invoice invoice) {
    this.invoice = invoice;
  }

  public CustomerOrder getCustomerOrder() {
    return customerOrder;
  }

  public void setCustomerOrder(CustomerOrder customerOrder) {
    this.customerOrder = customerOrder;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public LocalDateTime getDepositDate() {
    return depositDate;
  }

  public void setDepositDate(LocalDateTime depositDate) {
    this.depositDate = depositDate;
  }

  public Float getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(Float depositAmount) {
    this.depositAmount = depositAmount;
  }

  public List<AccountingRecord> getAccountingRecords() {
    return accountingRecords;
  }

  public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
    this.accountingRecords = accountingRecords;
  }

  public Payment getOriginPayment() {
    return originPayment;
  }

  public void setOriginPayment(Payment originPayment) {
    this.originPayment = originPayment;
  }

  public Boolean getIsCancelled() {
    return isCancelled;
  }

  public void setIsCancelled(Boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

}
