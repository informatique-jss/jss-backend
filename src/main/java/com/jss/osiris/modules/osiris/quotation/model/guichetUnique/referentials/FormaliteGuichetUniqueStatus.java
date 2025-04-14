package com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@DoNotAudit
@Table(name = "STATUS")
public class FormaliteGuichetUniqueStatus implements Serializable, ICode {
    public static String SIGNATURE_PENDING = "SIGNATURE_PENDING";
    public static String AMENDMENT_SIGNATURE_PENDING = "AMENDMENT_SIGNATURE_PENDING";
    public static String SIGNED = "SIGNED";
    public static String PAYMENT_PENDING = "PAYMENT_PENDING";
    public static String PAYMENT_VALIDATION_PENDING = "PAYMENT_VALIDATION_PENDING";
    public static String AMENDMENT_PAYMENT_PENDING = "AMENDMENT_PAYMENT_PENDING";
    public static String AMENDMENT_PAYMENT_VALIDATION_PENDING = "AMENDMENT_PAYMENT_VALIDATION_PENDING";
    public static String VALIDATION_PENDING = "VALIDATION_PENDING";

    // Refused status
    public static String AMENDMENT_PENDING = "AMENDMENT_PENDING";
    public static String ERROR_INSEE_EXISTS_PM = "ERROR_INSEE_EXISTS_PM";
    public static String ERROR_INSEE_EXISTS_PP = "ERROR_INSEE_EXISTS_PP";
    public static String ERROR_DECLARATION_INSEE = "ERROR_DECLARATION_INSEE";
    public static String ERROR = "ERROR";
    public static String EXPIRED = "EXPIRED";
    public static String REJECTED = "REJECTED";

    // Validated status
    public static String VALIDATED_DGFIP = "VALIDATED_DGFIP";
    public static String VALIDATED_PARTNER = "VALIDATED_PARTNER";
    public static String VALIDATED = "VALIDATED";

    public FormaliteGuichetUniqueStatus(String code) {
        this.code = code;
    }

    public FormaliteGuichetUniqueStatus() {
    }

    @Id
    private String code;

    @Column(columnDefinition = "TEXT")
    @JsonView({ JacksonViews.OsirisDetailedView.class })
    private String label;

    @Column(nullable = false)
    private Boolean isCloseState;

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

    public Boolean getIsCloseState() {
        return isCloseState;
    }

    public void setIsCloseState(Boolean isCloseState) {
        this.isCloseState = isCloseState;
    }

}
