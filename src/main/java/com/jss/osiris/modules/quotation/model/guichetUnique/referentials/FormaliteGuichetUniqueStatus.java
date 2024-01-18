package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
@Table(name = "STATUS")
public class FormaliteGuichetUniqueStatus implements Serializable, ICode {
    public static String SIGNATURE_PENDING = "SIGNATURE_PENDING";
    public static String AMENDMENT_SIGNATURE_PENDING = "AMENDMENT_SIGNATURE_PENDING";
    public static String SIGNED = "SIGNED";
    public static String PAYMENT_PENDING = "PAYMENT_PENDING";
    public static String PAYMENT_VALIDATION_PENDING = "PAYMENT_VALIDATION_PENDING";
    public static String AMENDMENT_PAYMENT_PENDING = "AMENDMENT_PAYMENT_PENDING";
    public static String AMENDMENT_PAYMENT_VALIDATION_PENDING = "AMENDMENT_PAYMENT_VALIDATION_PENDING";
    public static String AMENDMENT_PENDING = "AMENDMENT_PENDING";

    public FormaliteGuichetUniqueStatus(String code) {
        this.code = code;
    }

    public FormaliteGuichetUniqueStatus() {
    }

    @Id
    private String code;

    @Column(columnDefinition = "TEXT")
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
