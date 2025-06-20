package com.jss.osiris.libs.batch.model;

import java.io.Serializable;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class BatchCategory implements IId, Serializable {

    public static String GUICHET_UNIQUE = "GUICHET_UNIQUE";
    public static String INFOGREFFE = "INFOGREFFE";
    public static String INDEXATION = "INDEXATION";
    public static String ACCOUNTING = "ACCOUNTING";
    public static String SYSTEM = "SYSTEM";
    public static String MISCELLANEOUS = "MISCELLANEOUS";
    public static String REMINDERS = "REMINDERS";
    public static String REFERENTIALS = "REFERENTIALS";
    public static String MAILS = "MAILS";
    @Id
    @SequenceGenerator(name = "batch_category_sequence", sequenceName = "batch_category_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_category_sequence")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 20)
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
