package com.jss.osiris.modules.quotation.model;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;

public class AttachmentTypeMailQuery {
    private List<AttachmentType> attachmentTypes;
    private List<TypeDocument> typeDocument;
    private String comment;
    private Boolean sendToMe;
    private Boolean copyToMe;

    public List<AttachmentType> getAttachmentTypes() {
        return attachmentTypes;
    }

    public void setAttachmentTypes(List<AttachmentType> attachmentTypes) {
        this.attachmentTypes = attachmentTypes;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getSendToMe() {
        return sendToMe;
    }

    public void setSendToMe(Boolean sendToMe) {
        this.sendToMe = sendToMe;
    }

    public List<TypeDocument> getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(List<TypeDocument> typeDocument) {
        this.typeDocument = typeDocument;
    }

    public Boolean getCopyToMe() {
        return copyToMe;
    }

    public void setCopyToMe(Boolean copyToMe) {
        this.copyToMe = copyToMe;
    }

}
