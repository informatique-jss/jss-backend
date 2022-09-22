package com.jss.osiris.modules.invoicing.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.jss.osiris.modules.accounting.model.AccountingRecord;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Entity
public class Payment implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	private String label;

	@Column(nullable = false)
	private LocalDateTime paymentDate;

	@Column(nullable = false)
	private Float paymentAmount;

	@ManyToOne
	@JoinColumn(name = "id_payment_way")
	private PaymentWay paymentWay;

	@OneToMany(mappedBy = "payment", fetch = FetchType.EAGER)
	private List<AccountingRecord> accountingRecords;

	@ManyToOne
	@JoinColumn(name = "id_invoice")
	private Invoice invoice;

	@ManyToOne
	@JoinColumn(name = "id_customer_order")
	private CustomerOrder customerOrder;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Float getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public PaymentWay getPaymentWay() {
		return paymentWay;
	}

	public void setPaymentWay(PaymentWay paymentWay) {
		this.paymentWay = paymentWay;
	}

	public List<AccountingRecord> getAccountingRecords() {
		return accountingRecords;
	}

	public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
		this.accountingRecords = accountingRecords;
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

}
