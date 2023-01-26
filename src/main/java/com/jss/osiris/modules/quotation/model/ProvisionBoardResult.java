package com.jss.osiris.modules.quotation.model;


public class ProvisionBoardResult {

	private final Integer DEFAULT_PRIORITY = 20;

	private Integer employee;

	private Integer nbProvision;

	private String status;

	private Integer priority = DEFAULT_PRIORITY;


	
	public ProvisionBoardResult(Integer employee, Integer nbProvision, String status, Integer p1, Integer p2, Integer p3) {
		this.employee = employee;
		this.nbProvision = nbProvision;
		this.status = status;


		if (p1 == null) {
			this.priority = maxPriority(p2, p3);
		} else if (p2 == null) {
			this.priority = maxPriority(p1, p3);
		} else if (p3 == null) {
			this.priority = maxPriority(p2, p3);
		} else if (p1 > p2 && p1 > p3) {
			this.priority = p1;
		} else if (p2 > p1 && p2 > p3) {
			this.priority = p2;
		} else {
			this.priority = p3;
		}
	}

	private Integer maxPriority(Integer p1, Integer p2) {
		Integer res = DEFAULT_PRIORITY;

		if (p1 != null) {
			if (p2 !=null) {
				if (p1 > p2) {
					res = p1;
				} else {
					res = p2;
				}
			} else {
				res = p1;
			}
		} else {
			if (p2 !=null) {
				res = p2;
			}
		}
		return res;
	}

	public Integer getEmployee() {
		return employee;
	}

	public void setEmployee(Integer employee) {
		this.employee = employee;
	}

	public Integer getNbProvision() {
		return nbProvision;
	}

	public void setNbProvision(Integer nbProvision) {
		this.nbProvision = nbProvision;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "ProvisionBoardResult ["+this.employee+", "+this.nbProvision+", "+this.status+", "+this.priority+"]";
	}



}
