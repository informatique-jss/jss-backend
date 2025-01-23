package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

public interface IPaperSetResult {
	Integer getId();

	Boolean getIsValidated();

	Boolean getIsCancelled();

	String getPaperSetTypeLabel();

	Integer getCustomerOrderId();

	String getCustomerOrderStatus();

	String getTiersLabel();

	String getTiersId();

	String getResponsableLabel();

	String getResponsableId();

	String getAffaireLabel();

	String getServicesLabel();

	Integer getLocationNumber();

	String getCreationComment();

	String getValidationComment();

	String getValidatedBy();

	LocalDateTime getValidationDateTime();

	String getCreatedBy();

	LocalDateTime getCreatedDateTime();
}