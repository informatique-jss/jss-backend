package com.jss.osiris.modules.quotation.model;

import java.util.List;

public class IStatus {

	protected Integer id;

	protected String label;

	protected String code;

	protected String icon;

	protected List<IStatus> successors;

	protected List<IStatus> predecessors;

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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<IStatus> getSuccessors() {
		return successors;
	}

	public void setSuccessors(List<IStatus> successors) {
		this.successors = successors;
	}

	public List<IStatus> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(List<IStatus> predecessors) {
		this.predecessors = predecessors;
	}

}
