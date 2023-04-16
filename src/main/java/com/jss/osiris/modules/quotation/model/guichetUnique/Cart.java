package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.invoicing.model.Invoice;

@Entity
public class Cart {

    @Id
    private Integer id;

    private String type;
    private long cartID;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formalite_guichet_unique")
    @JsonIgnoreProperties(value = { "carts" }, allowSetters = true)
    private FormaliteGuichetUnique formaliteGuichetUnique;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "cart", "debours" }, allowSetters = true)
    private List<CartRate> cartRates;
    private long total;
    private long mipOrderNum;
    private String paymentDate;
    private String paymentType;
    private String status;
    private String created;
    private String updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "id_invoice")
    private Invoice invoice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCartID() {
        return cartID;
    }

    public void setCartID(long cartID) {
        this.cartID = cartID;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getMipOrderNum() {
        return mipOrderNum;
    }

    public void setMipOrderNum(long mipOrderNum) {
        this.mipOrderNum = mipOrderNum;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public FormaliteGuichetUnique getFormaliteGuichetUnique() {
        return formaliteGuichetUnique;
    }

    public void setFormaliteGuichetUnique(FormaliteGuichetUnique formaliteGuichetUnique) {
        this.formaliteGuichetUnique = formaliteGuichetUnique;
    }

    public List<CartRate> getCartRates() {
        return cartRates;
    }

    public void setCartRates(List<CartRate> cartRates) {
        this.cartRates = cartRates;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
