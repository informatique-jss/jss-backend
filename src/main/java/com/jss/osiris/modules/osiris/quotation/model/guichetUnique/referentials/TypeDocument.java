package com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;

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

    public TypeDocument() {
    }

    public TypeDocument(String code) {
        this.code = code;
    }

    public static String UNSIGNED_SYNTHESES_DOCUMENT_CODE = "PJ_99";
    public static String SIGNED_SYNTHESES_DOCUMENT_CODE = "PJ_115";

    @Id
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
            JacksonViews.OsirisDetailedView.class })
    private String code;

    private Boolean isToDownloadOnProvision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_attachment_type")
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
            JacksonViews.OsirisDetailedView.class })
    private AttachmentType attachmentType;

    @Column(columnDefinition = "TEXT")
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
            JacksonViews.OsirisDetailedView.class })
    private String label;

    @Column(columnDefinition = "TEXT")
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
            JacksonViews.OsirisDetailedView.class })
    private String customLabel;

    @OneToMany(mappedBy = "typeDocumentAttachment", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "typeDocumentAttachment" }, allowSetters = true)
    @JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.MyJssListView.class,
            JacksonViews.OsirisDetailedView.class })
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

    public static String getUNSIGNED_SYNTHESES_DOCUMENT_CODE() {
        return UNSIGNED_SYNTHESES_DOCUMENT_CODE;
    }

    public static void setUNSIGNED_SYNTHESES_DOCUMENT_CODE(String uNSIGNED_SYNTHESES_DOCUMENT_CODE) {
        UNSIGNED_SYNTHESES_DOCUMENT_CODE = uNSIGNED_SYNTHESES_DOCUMENT_CODE;
    }

    public static String getSIGNED_SYNTHESES_DOCUMENT_CODE() {
        return SIGNED_SYNTHESES_DOCUMENT_CODE;
    }

    public static void setSIGNED_SYNTHESES_DOCUMENT_CODE(String sIGNED_SYNTHESES_DOCUMENT_CODE) {
        SIGNED_SYNTHESES_DOCUMENT_CODE = sIGNED_SYNTHESES_DOCUMENT_CODE;
    }

    public String getCustomLabel() {
        return customLabel;
    }

    public void setCustomLabel(String customLabel) {
        this.customLabel = customLabel;
    }
}
