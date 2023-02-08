package com.jss.osiris.modules.quotation.model;


public class ProvisionBoardDisplayedResult {

	// Status
	public final static String STATUS_NEW = "NEW"; // Priorite 1
	public final static String STATUS_IN_PROGRESS = "IN_PROGRESS"; // Priorite 2
	public final static String STATUS_WAITING = "WAITING"; // Priorite 3
	public final static String STATUS_WAITING_GREFFE = "WAITING_GREFFE"; // Priorite 4
	public final static String STATUS_WAITING_AUTHORITY = "WAITING_AUTHORITY"; // Priorite 4
	public final static String STATUS_WAITING_CONFRERE_PUBLISHED = "WAITING_CONFRERE_PUBLISHED"; // Priorite 4
	public final static String STATUS_VALIDATE_GREFFE = "VALIDATE_GREFFE"; // Priorite 5
	public final static String STATUS_REFUSED_GREFFE = "REFUSED_GREFFE"; // Priorite 5
	public final static String STATUS_PUBLISHED = "PUBLISHED"; // Priorite 6
	public final static String STATUS_DONE = "DONE"; // Priorite 7

	private Integer employee;

	private Integer nbProvisionNew;
	private Integer nbProvisionInProgress;
	private Integer nbProvisionWaiting;
	private Integer nbProvisionWaitingGreffe;
	private Integer nbProvisionWaitingAuthority;
	private Integer nbProvisionConfrerePublished;
	private Integer nbProvisionValidateGreffe;
	private Integer nbProvisionRefusedGreffe;
	private Integer nbProvisionPublished;
	private Integer nbProvisionDone;


	
	public ProvisionBoardDisplayedResult(Integer employee) {
		this.employee = employee;

		nbProvisionNew = 0;
		nbProvisionInProgress = 0;
		nbProvisionWaiting = 0;
		nbProvisionWaitingGreffe = 0;
		nbProvisionWaitingAuthority = 0;
		nbProvisionConfrerePublished = 0;
		nbProvisionValidateGreffe = 0;
		nbProvisionRefusedGreffe = 0;
		nbProvisionPublished = 0;
		nbProvisionDone = 0;
	}


	public Integer getEmployee() {
		return employee;
	}

	public void setEmployee(Integer employee) {
		this.employee = employee;
	}


	public Integer getNbProvisionNew() {
		return nbProvisionNew;
	}


	public void addNbProvisionNew(Integer nbProvisionNew) {
		this.nbProvisionNew += nbProvisionNew;
	}


	public Integer getNbProvisionInProgress() {
		return nbProvisionInProgress;
	}


	public void addNbProvisionInProgress(Integer nbProvisionInProgress) {
		this.nbProvisionInProgress += nbProvisionInProgress;
	}


	public Integer getNbProvisionWaiting() {
		return nbProvisionWaiting;
	}


	public void addNbProvisionWaiting(Integer nbProvisionWaiting) {
		this.nbProvisionWaiting += nbProvisionWaiting;
	}


	public Integer getNbProvisionWaitingGreffe() {
		return nbProvisionWaitingGreffe;
	}


	public void addNbProvisionWaitingGreffe(Integer nbProvisionWaitingGreffe) {
		this.nbProvisionWaitingGreffe += nbProvisionWaitingGreffe;
	}


	public Integer getNbProvisionWaitingAuthority() {
		return nbProvisionWaitingAuthority;
	}


	public void addNbProvisionWaitingAuthority(Integer nbProvisionWaitingAuthority) {
		this.nbProvisionWaitingAuthority += nbProvisionWaitingAuthority;
	}


	public Integer getNbProvisionConfrerePublished() {
		return nbProvisionConfrerePublished;
	}


	public void addNbProvisionConfrerePublished(Integer nbProvisionConfrerePublished) {
		this.nbProvisionConfrerePublished += nbProvisionConfrerePublished;
	}


	public Integer getNbProvisionValidateGreffe() {
		return nbProvisionValidateGreffe;
	}


	public void addNbProvisionValidateGreffe(Integer nbProvisionValidateGreffe) {
		this.nbProvisionValidateGreffe += nbProvisionValidateGreffe;
	}


	public Integer getNbProvisionRefusedGreffe() {
		return nbProvisionRefusedGreffe;
	}


	public void addNbProvisionRefusedGreffe(Integer nbProvisionRefusedGreffe) {
		this.nbProvisionRefusedGreffe += nbProvisionRefusedGreffe;
	}


	public Integer getNbProvisionPublished() {
		return nbProvisionPublished;
	}


	public void addNbProvisionPublished(Integer nbProvisionPublished) {
		this.nbProvisionPublished += nbProvisionPublished;
	}


	public Integer getNbProvisionDone() {
		return nbProvisionDone;
	}


	public void addNbProvisionDone(Integer nbProvisionDone) {
		this.nbProvisionDone += nbProvisionDone;
	}

    public void updateProvisionBoardDisplayedResult(String aggregateStatus, Integer nbProvision) {
		switch(aggregateStatus) {
			case ProvisionBoardDisplayedResult.STATUS_NEW : 
				this.addNbProvisionNew(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_IN_PROGRESS : 
				this.addNbProvisionInProgress(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_WAITING : 
				this.addNbProvisionWaiting(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_WAITING_GREFFE : 
				this.addNbProvisionWaitingGreffe(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_WAITING_AUTHORITY : 
				this.addNbProvisionWaitingAuthority(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_WAITING_CONFRERE_PUBLISHED : 
				this.addNbProvisionConfrerePublished(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_VALIDATE_GREFFE : 
				this.addNbProvisionValidateGreffe(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_REFUSED_GREFFE : 
				this.addNbProvisionRefusedGreffe(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_PUBLISHED : 
				this.addNbProvisionPublished(nbProvision);
				break;
			case ProvisionBoardDisplayedResult.STATUS_DONE : 
				this.addNbProvisionDone(nbProvision);
				break;
		}

    }


	@Override
	public String toString() {
		return "ProvisionBoardDisplayedResult [employee=" + employee + ", nbProvisionNew=" + nbProvisionNew
				+ ", nbProvisionInProgress=" + nbProvisionInProgress + ", nbProvisionWaiting=" + nbProvisionWaiting
				+ ", nbProvisionWaitingGreffe=" + nbProvisionWaitingGreffe + ", nbProvisionWaitingAuthority="
				+ nbProvisionWaitingAuthority + ", nbProvisionConfrerePublished=" + nbProvisionConfrerePublished
				+ ", nbProvisionValidateGreffe=" + nbProvisionValidateGreffe + ", nbProvisionRefusedGreffe="
				+ nbProvisionRefusedGreffe + ", nbProvisionPublished=" + nbProvisionPublished + ", nbProvisionDone="
				+ nbProvisionDone + "]";
	}




}
