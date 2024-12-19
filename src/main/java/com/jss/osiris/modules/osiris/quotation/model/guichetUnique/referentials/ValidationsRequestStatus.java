package com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DoNotAudit
public class ValidationsRequestStatus implements Serializable, ICode {

    public static String MSA_ACCEPTATION_PENDING = "MSA_ACCEPTATION_PENDING";
    public static String VALIDATION_PENDING = "VALIDATION_PENDING";
    public static String AMENDED = "AMENDED";

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
