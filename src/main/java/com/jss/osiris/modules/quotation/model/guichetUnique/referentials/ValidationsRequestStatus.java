package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
public class ValidationsRequestStatus implements Serializable, ICode {

    public static String MSA_ACCEPTATION_PENDING = "MSA_ACCEPTATION_PENDING";
    public static String VALIDATION_PENDING = "VALIDATION_PENDING";

    public ValidationsRequestStatus(String code) {
        this.code = code;
    }

    public ValidationsRequestStatus() {
    }

    @Id
    private String code;

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

}
