package com.jss.osiris.modules.osiris.quotation.model;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

public class IWorkflowElement implements IId {

    private Integer id;
    private List<? extends IWorkflowElement> successors;
    private List<? extends IWorkflowElement> predecessors;
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

    public List<? extends IWorkflowElement> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<? extends IWorkflowElement> successors) {
        this.successors = successors;
    }

    public List<? extends IWorkflowElement> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<? extends IWorkflowElement> predecessors) {
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
