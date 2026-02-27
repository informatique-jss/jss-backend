package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDateTime;

public interface AssoAffaireOrderSearchResult {

	public String getAffaireLabel();

	public String getAffaireAddress();

	public String getTiersLabel();

	public String getResponsableLabel();

	public String getConfrereLabel();

	public Integer getSalesEmployeeId();

	public Integer getAssignedToId();

	public String getProvisionTypeLabel();

	public String getServiceTypeLabel();

	public String getStatusLabel();

	public String getAssoId();

	public String getCustomerOrderId();

	public String getProvisionId();

	public Boolean getIsEmergency();

	public Boolean getIsPriority();

	public String getWaitedCompetentAuthorityLabel();

	public String getCompetentAuthorityLabel();

	public LocalDateTime getProvisionStatusDatetime();

	public LocalDateTime getProvisionCreatedDatetime();

	public LocalDateTime getCreatedDate();
}