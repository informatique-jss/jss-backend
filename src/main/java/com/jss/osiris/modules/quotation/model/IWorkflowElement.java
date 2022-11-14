package com.jss.osiris.modules.quotation.model;

import java.util.ArrayList;

import com.jss.osiris.modules.miscellaneous.model.IId;

public class IWorkflowElement implements IId {

    private Integer id;
    private ArrayList<IWorkflowElement> successors;
    private ArrayList<IWorkflowElement> predecessors;
    private String label;
    private String icon;
    private String code;
    private Boolean isOpenState;
    private Boolean isCloseState;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<IWorkflowElement> getSuccessors() {
        return successors;
    }

    public void setSuccessors(ArrayList<IWorkflowElement> successors) {
        this.successors = successors;
    }

    public ArrayList<IWorkflowElement> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(ArrayList<IWorkflowElement> predecessors) {
        this.predecessors = predecessors;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsOpenState() {
        return isOpenState;
    }

    public void setIsOpenState(Boolean isOpenState) {
        this.isOpenState = isOpenState;
    }

    public Boolean getIsCloseState() {
        return isCloseState;
    }

    public void setIsCloseState(Boolean isCloseState) {
        this.isCloseState = isCloseState;
    }

}
