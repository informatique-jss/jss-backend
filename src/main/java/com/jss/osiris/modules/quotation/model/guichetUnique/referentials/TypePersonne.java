package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import com.jss.osiris.libs.search.model.DoNotAudit;
import com.jss.osiris.modules.miscellaneous.model.ICode;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DoNotAudit
public class TypePersonne implements Serializable, ICode {
    public TypePersonne(String code) {
        this.code = code;
    }

    public TypePersonne() {
    }

    @Id
    private String code;

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
