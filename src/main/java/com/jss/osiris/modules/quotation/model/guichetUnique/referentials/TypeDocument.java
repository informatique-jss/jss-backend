package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
public class TypeDocument implements Serializable, ICode {
    public TypeDocument(String code) {
        this.code = code;
    }

    public TypeDocument() {
    }

    @Id
    private String code;

    private Boolean isToDownloadOnProvision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_attachment_type")
    private AttachmentType attachmentType;

    @Column(columnDefinition = "TEXT")
    private String label;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getIsToDownloadOnProvision() {
        return isToDownloadOnProvision;
    }

    public void setIsToDownloadOnProvision(Boolean isToDownloadOnProvision) {
        this.isToDownloadOnProvision = isToDownloadOnProvision;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }

}
