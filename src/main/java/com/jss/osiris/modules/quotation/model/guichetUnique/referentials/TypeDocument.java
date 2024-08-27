package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@DoNotAudit
public class TypeDocument implements ICode, IAttachment {

    public TypeDocument(String code) {
        this.code = code;
    }

    public static String UNSIGNED_SYNTHESES_DOCUMENT_CODE = "PJ_99";
    public static String SIGNED_SYNTHESES_DOCUMENT_CODE = "PJ_115";

    public static String UNSIGNED_BE_DOCUMENT_CODE = "PJ_119";
    public static String SIGNED_BE_DOCUMENT_CODE = "PJ_120";

    @Id
    private String code;

    private Boolean isToDownloadOnProvision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_attachment_type")
    private AttachmentType attachmentType;

    @Column(columnDefinition = "TEXT")
    private String label;

    @OneToMany(mappedBy = "typeDocumentAttachment", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "typeDocumentAttachment" }, allowSetters = true)
    private List<Attachment> attachments;

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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
