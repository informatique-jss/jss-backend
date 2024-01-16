package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;

public interface AssoAffaireOrderSearchResult {

	public String getAffaireLabel();

	public String getAffaireAddress();

	public String getTiersLabel();

	public String getResponsableLabel();

	public String getConfrereLabel();

	public String getResponsibleId();

	public String getAssignedToId();

	public String getProvisionTypeLabel();

	public String getStatusLabel();

	public String getAssoId();

	public String getCustomerOrderId();

	public String getProvisionId();

	public Boolean getIsEmergency();

	public String getWaitedCompetentAuthorityLabel();

	public String getCompetentAuthorityLabel();

	public LocalDateTime getProvisionStatusDatetime();

	public LocalDateTime getProvisionCreatedDatetime();
}