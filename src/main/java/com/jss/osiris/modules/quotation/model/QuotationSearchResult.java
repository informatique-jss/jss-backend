package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;

public interface QuotationSearchResult {
    public String getTiersLabel();

    public String getCustomerOrderLabel();

    public String getQuotationStatus();

    public LocalDateTime getCreatedDate();

    public Integer getSalesEmployeeId();

    public Integer getAssignedToEmployeeId();

    public String getQuotationDescription();

    public Integer getQuotationId();

    public Integer getResponsableId();

    public Integer getTiersId();

    public String getServiceTypeLabel();

    public String getAffaireLabel();

    public Float getTotalPrice();

    public Integer getSimpleProvisionNbr();

    public LocalDateTime getLastStatusUpdate();

    public String getCustomerOrderOriginLabel();
}
