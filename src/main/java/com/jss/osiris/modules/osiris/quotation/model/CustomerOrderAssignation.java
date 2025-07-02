package com.jss.osiris.modules.osiris.quotation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.profile.model.Employee;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_order_assignation", columnList = "id_customer_order"),
        @Index(name = "idx_order_assignation_unique", columnList = "id_customer_order,id_assignation_type", unique = true) })
public class CustomerOrderAssignation {

    @Id
    @SequenceGenerator(name = "customer_order_assignation_sequence", sequenceName = "customer_order_assignation_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_order_assignation_sequence")
    @JsonView({ JacksonViews.OsirisListView.class })
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_customer_order")
    @JsonIgnore
    private CustomerOrder customerOrder;

    @ManyToOne
    @JoinColumn(name = "id_assignation_type")
    @JsonView({ JacksonViews.OsirisListView.class })
    private AssignationType assignationType;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    @JsonView({ JacksonViews.OsirisListView.class })
    private Employee employee;

    private Boolean isAssigned;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public AssignationType getAssignationType() {
        return assignationType;
    }

    public void setAssignationType(AssignationType assignationType) {
        this.assignationType = assignationType;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Boolean getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(Boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

}
