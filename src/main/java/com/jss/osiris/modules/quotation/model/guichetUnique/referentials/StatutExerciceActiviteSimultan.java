package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import com.jss.osiris.modules.miscellaneous.model.ICode;

@Entity
public class StatutExerciceActiviteSimultan implements Serializable, ICode {
    public StatutExerciceActiviteSimultan(String code) {
        this.code = code;
    }

    public StatutExerciceActiviteSimultan() {
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
