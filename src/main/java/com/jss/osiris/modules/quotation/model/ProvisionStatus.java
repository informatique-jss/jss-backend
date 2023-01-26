package com.jss.osiris.modules.quotation.model;

import java.util.List;

/**
 * Status avec icon successor predecessor ... 
 * announcement bodacc etc
 */
public abstract class ProvisionStatus {

	public abstract Integer getId();

	public abstract void setId(Integer id);

	public abstract String getLabel();

	public abstract void setLabel(String label);

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract String getIcon();

	public abstract void setIcon(String icon);

	public abstract Boolean getIsOpenState();

	public abstract void setIsOpenState(Boolean isOpenState);

	public abstract Boolean getIsCloseState();

	public abstract void setIsCloseState(Boolean isCloseState);

	public abstract <T> List<T> getSuccessors();

	public abstract <T> List<T> getPredecessors();

	public abstract String getAggregateLabel();

	public abstract Integer getAggregatePriority();

	public boolean equals(ProvisionStatus p2) {
		return this.getCode().equals(p2.getCode());
	}

}
