package com.jss.osiris.modules.osiris.quotation.model;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

public class AttachmentMailRequest {
    private CustomerOrder customerOrder;
    private AssoAffaireOrder assoAffaireOrder;
    private List<Attachment> attachements;
    private String comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
