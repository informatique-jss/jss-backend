package com.jss.osiris.modules.quotation.model;

import java.util.ArrayList;

import com.jss.osiris.modules.profile.model.Employee;

public class AffaireSearch {

    private Employee responsible;
    private Employee assignedTo;
    private String label;
    private ArrayList<IWorkflowElement> status;

    public Employee getResponsible() {
        return responsible;
    }

    public void setResponsible(Employee responsible) {
        this.responsible = responsible;
    }

    public Employee getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Employee assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<IWorkflowElement> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<IWorkflowElement> status) {
        this.status = status;
    }

}
