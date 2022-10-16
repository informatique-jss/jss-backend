package com.jss.osiris.modules.quotation.model;

import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;

public class AffaireSearch {

    private Employee responsible;
    private Employee assignedTo;
    private List<AffaireStatus> affaireStatus;
    private String label;

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

    public List<AffaireStatus> getAffaireStatus() {
        return affaireStatus;
    }

    public void setAffaireStatus(List<AffaireStatus> affaireStatus) {
        this.affaireStatus = affaireStatus;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
