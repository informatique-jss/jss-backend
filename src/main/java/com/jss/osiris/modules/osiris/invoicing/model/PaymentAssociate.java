package com.jss.osiris.modules.osiris.invoicing.model;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public class PaymentAssociate {
    private Payment payment;
    private List<Invoice> invoices;
    private List<CustomerOrder> customerOrders;
    private Affaire affaireRefund;
    private Tiers tiersRefund;
    private Responsable responsableOrder;
    private List<Double> byPassAmount;

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

    public List<Double> getByPassAmount() {
        return byPassAmount;
    }

    public void setByPassAmount(List<Double> byPassAmount) {
        this.byPassAmount = byPassAmount;
    }

    public Tiers getTiersRefund() {
        return tiersRefund;
    }

    public void setTiersRefund(Tiers tiersRefund) {
        this.tiersRefund = tiersRefund;
    }

    public Responsable getResponsableOrder() {
        return responsableOrder;
    }

    public void setResponsableOrder(Responsable responsableOrder) {
        this.responsableOrder = responsableOrder;
    }

}
