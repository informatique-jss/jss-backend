package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.quotation.model.Debour;

@Entity
public class CartRate {

    @Id
    @SequenceGenerator(name = "guichet_unique_cart_rate_sequence", sequenceName = "guichet_unique_cart_rate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guichet_unique_cart_rate_sequence")
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cart")
    private Cart cart;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rate")
    private Rate rate;

    private long quantity;
    private long amount;
    private boolean isForcedManually;
    private long htAmount;
    private String recipientCode;
    private String recipientName;
    private long subTotal;

    @OneToMany(mappedBy = "cartRate", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "cartRate" }, allowSetters = true)
    private List<Debour> debours;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public boolean isForcedManually() {
        return isForcedManually;
    }

    public void setForcedManually(boolean isForcedManually) {
        this.isForcedManually = isForcedManually;
    }

    public long getHtAmount() {
        return htAmount;
    }

    public void setHtAmount(long htAmount) {
        this.htAmount = htAmount;
    }

    public String getRecipientCode() {
        return recipientCode;
    }

    public void setRecipientCode(String recipientCode) {
        this.recipientCode = recipientCode;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public long getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(long subTotal) {
        this.subTotal = subTotal;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Debour> getDebours() {
        return debours;
    }

    public void setDebours(List<Debour> debours) {
        this.debours = debours;
    }

}
