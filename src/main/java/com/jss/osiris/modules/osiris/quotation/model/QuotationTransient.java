package com.jss.osiris.modules.osiris.quotation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuotationTransient {
	private Integer id;
	private String affairesList;
	private String servicesList;
	private Boolean hasMissingInformations;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAffairesList() {
		return affairesList;
	}

	public void setAffairesList(String affairesList) {
		this.affairesList = affairesList;
	}

	public String getServicesList() {
		return servicesList;
	}

	public void setServicesList(String servicesList) {
		this.servicesList = servicesList;
	}

	public Boolean getHasMissingInformations() {
		return hasMissingInformations;
	}

	public void setHasMissingInformations(Boolean hasMissingInformations) {
		this.hasMissingInformations = hasMissingInformations;
	}

}
