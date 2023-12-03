package com.jss.osiris.modules.invoicing.model;

import java.util.List;

import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.tiers.model.Tiers;

public class PaymentAssociate {
    private Payment payment;
    private List<Invoice> invoices;
    private List<CustomerOrder> customerOrders;
    private Affaire affaireRefund;
    private Tiers tiersRefund;
    private Tiers tiersOrder;
    private Confrere confrereRefund;
    private List<Float> byPassAmount;

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<CustomerOrder> customerOrders) {
        this.customerOrders = customerOrders;
    }

    public Affaire getAffaireRefund() {
        return affaireRefund;
    }

    public void setAffaireRefund(Affaire affaire) {
        this.affaireRefund = affaire;
    }

    public List<Float> getByPassAmount() {
        return byPassAmount;
    }

    public void setByPassAmount(List<Float> byPassAmount) {
        this.byPassAmount = byPassAmount;
    }

    public Confrere getConfrereRefund() {
        return confrereRefund;
    }

    public Tiers getTiersRefund() {
        return tiersRefund;
    }

    public void setTiersRefund(Tiers tiersRefund) {
        this.tiersRefund = tiersRefund;
    }

    public void setConfrereRefund(Confrere confrereRefund) {
        this.confrereRefund = confrereRefund;
    }

    public Tiers getTiersOrder() {
        return tiersOrder;
    }

    public void setTiersOrder(Tiers tiersOrder) {
        this.tiersOrder = tiersOrder;
    }

}
