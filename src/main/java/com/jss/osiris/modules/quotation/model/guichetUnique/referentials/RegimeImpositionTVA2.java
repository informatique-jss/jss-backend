package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
public class RegimeImpositionTVA2 implements Serializable, ICode {
    public RegimeImpositionTVA2(String code) {
        this.code = code;
    }

    public RegimeImpositionTVA2() {
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
