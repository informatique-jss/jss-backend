package com.jss.osiris.modules.invoicing.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;

@Entity
public class OwncloudGreffeInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_owncloud_greffe_file")
    private OwncloudGreffeFile owncloudGreffeFile;

    private String numero;

    private LocalDate date;

    private String customerReference;

    private Float preTaxPrice;

    private Float vatPrice;

    private Float totalTaxedPrice;

    private Float nonTaxablePrice;

    private Float totalPrice;

    @ManyToOne
    @JoinColumn(name = "id_customer_order")
    private CustomerOrder customerOrder;

    @ManyToOne
    @JoinColumn(name = "id_debour")
    private Debour debour;

    @ManyToOne
    @JoinColumn(name = "id_invoice")
    private Invoice invoice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Float getPreTaxPrice() {
        return preTaxPrice;
    }

    public void setPreTaxPrice(Float preTaxPrice) {
        this.preTaxPrice = preTaxPrice;
    }

    public Float getVatPrice() {
        return vatPrice;
    }

    public void setVatPrice(Float vatPrice) {
        this.vatPrice = vatPrice;
    }

    public Float getTotalTaxedPrice() {
        return totalTaxedPrice;
    }

    public void setTotalTaxedPrice(Float totalTaxedPrice) {
        this.totalTaxedPrice = totalTaxedPrice;
    }

    public Float getNonTaxablePrice() {
        return nonTaxablePrice;
    }

    public void setNonTaxablePrice(Float nonTaxablePrice) {
        this.nonTaxablePrice = nonTaxablePrice;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OwncloudGreffeFile getOwncloudGreffeFile() {
        return owncloudGreffeFile;
    }

    public void setOwncloudGreffeFile(OwncloudGreffeFile owncloudGreffeFile) {
        this.owncloudGreffeFile = owncloudGreffeFile;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public Debour getDebour() {
        return debour;
    }

    public void setDebour(Debour debour) {
        this.debour = debour;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

}
