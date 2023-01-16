package com.jss.osiris.modules.quotation.model;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Attachment;

public class AttachmentMailRequest {
    private CustomerOrder customerOrder;
    private AssoAffaireOrder assoAffaireOrder;
    private List<Attachment> attachements;
    private Boolean sendToMe;

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public AssoAffaireOrder getAssoAffaireOrder() {
        return assoAffaireOrder;
    }

    public void setAssoAffaireOrder(AssoAffaireOrder assoAffaireOrder) {
        this.assoAffaireOrder = assoAffaireOrder;
    }

    public List<Attachment> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<Attachment> attachements) {
        this.attachements = attachements;
    }

    public Boolean getSendToMe() {
        return sendToMe;
    }

    public void setSendToMe(Boolean sendToMe) {
        this.sendToMe = sendToMe;
    }

}
