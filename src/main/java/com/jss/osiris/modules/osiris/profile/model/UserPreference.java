package com.jss.osiris.modules.osiris.profile.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class UserPreference {

    public static final String CUSTOM_USER_PREFERENCE = "CUSTOM_USER_PREFERENCE";
    public static final String DEFAULT_USER_PREFERENCE = "DEFAULT_USER_PREFERENCE";

    @Id
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "id_employee")
    @JsonView({ JacksonViews.OsirisListView.class, JacksonViews.OsirisDetailedView.class })
    private Employee employee;

    private String code;

    @Column(columnDefinition = "TEXT")
    private String jsonValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }
}
