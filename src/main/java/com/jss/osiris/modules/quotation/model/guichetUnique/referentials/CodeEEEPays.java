package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DoNotAudit
public class CodeEEEPays implements Serializable, ICode {
    public CodeEEEPays() {
    }

    public CodeEEEPays(String code) {
        this.code = code;
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
