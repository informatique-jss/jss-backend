package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DoNotAudit
public class TypeDocument implements Serializable, ICode {

    public static String UNSIGNED_SYNTHESES_DOCUMENT_CODE = "PJ_99";
    public static String SIGNED_SYNTHESES_DOCUMENT_CODE = "PJ_115";

    public static String UNSIGNED_BE_DOCUMENT_CODE = "PJ_119";
    public static String SIGNED_BE_DOCUMENT_CODE = "PJ_120";

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
