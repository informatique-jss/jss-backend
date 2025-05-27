package com.jss.osiris.modules.osiris.quotation.model.guichetUnique;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.DoNotAudit;

import jakarta.persistence.CascadeType;
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
@DoNotAudit
@Table(indexes = {
        @Index(name = "idx_cart_rate_cart", columnList = "id_cart") })
public class CartRate implements Serializable {

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

}
