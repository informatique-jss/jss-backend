package com.jss.osiris.libs.batch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BatchStatus {
    public static String NEW = "NEW";
    public static String WAITING = "WAITING";
    public static String RUNNING = "RUNNING";
    public static String SUCCESS = "SUCCESS";
    public static String ERROR = "ERROR";
    public static String ACKNOWLEDGE = "ACKNOWLEDGE";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
