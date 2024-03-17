package com.jss.osiris.libs.batch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class BatchCategory implements IId {

    public static String GUICHET_UNIQUE = "GUICHET_UNIQUE";
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
