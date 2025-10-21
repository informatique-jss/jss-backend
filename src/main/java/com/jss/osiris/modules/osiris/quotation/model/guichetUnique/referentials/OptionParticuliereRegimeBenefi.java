package com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class OptionParticuliereRegimeBenefi implements Serializable, ICode {
    public OptionParticuliereRegimeBenefi(String code) {
        this.code = code;
    }

    public OptionParticuliereRegimeBenefi() {
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
