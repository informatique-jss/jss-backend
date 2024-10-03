package com.jss.osiris.modules.osiris.quotation.model;

import java.util.ArrayList;
import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public class AffaireSearch {

    private Employee responsible;
    private Employee assignedTo;
    private String label;
    private ArrayList<IWorkflowElement> status;
    private List<Tiers> customerOrders;
    private Affaire affaire;
    private CompetentAuthority waitedCompetentAuthority;
    private Boolean isMissingQueriesToManualRemind;
    private Employee commercial;
    private FormaliteGuichetUniqueStatus formaliteGuichetUniqueStatus;
    private String formaliteInfogreffeStatusCode;

    public List<Tiers> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(List<Tiers> customerOrders) {
        this.customerOrders = customerOrders;
    }

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

    public CompetentAuthority getWaitedCompetentAuthority() {
        return waitedCompetentAuthority;
    }

    public void setWaitedCompetentAuthority(CompetentAuthority waitedCompetentAuthority) {
        this.waitedCompetentAuthority = waitedCompetentAuthority;
    }

    public Affaire getAffaire() {
        return affaire;
    }

    public void setAffaire(Affaire affaire) {
        this.affaire = affaire;
    }

    public Boolean getIsMissingQueriesToManualRemind() {
        return isMissingQueriesToManualRemind;
    }

    public void setIsMissingQueriesToManualRemind(Boolean isMissingQueriesToManualRemind) {
        this.isMissingQueriesToManualRemind = isMissingQueriesToManualRemind;
    }

    public Employee getCommercial() {
        return commercial;
    }

    public void setCommercial(Employee commercial) {
        this.commercial = commercial;
    }

    public FormaliteGuichetUniqueStatus getFormaliteGuichetUniqueStatus() {
        return formaliteGuichetUniqueStatus;
    }

    public void setFormaliteGuichetUniqueStatus(FormaliteGuichetUniqueStatus formaliteGuichetUniqueStatus) {
        this.formaliteGuichetUniqueStatus = formaliteGuichetUniqueStatus;
    }

    public String getFormaliteInfogreffeStatusCode() {
        return formaliteInfogreffeStatusCode;
    }

    public void setFormaliteInfogreffeStatusCode(String formaliteInfogreffeStatusCode) {
        this.formaliteInfogreffeStatusCode = formaliteInfogreffeStatusCode;
    }

}
