package com.jss.osiris.modules.quotation.model;

public interface IPaperSetResult {
	Integer getId();

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
}
