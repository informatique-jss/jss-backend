package com.jss.osiris.libs.batch.model;

import com.jss.osiris.modules.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class BatchStatus implements IId {
    public static String NEW = "NEW";
    public static String WAITING = "WAITING";
    public static String RUNNING = "RUNNING";
    public static String SUCCESS = "SUCCESS";
    public static String ERROR = "ERROR";
    public static String ACKNOWLEDGE = "ACKNOWLEDGE";

    @Id
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, length = 100)
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
