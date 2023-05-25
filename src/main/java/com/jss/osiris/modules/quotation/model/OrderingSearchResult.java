package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;

public interface OrderingSearchResult {
    public String getTiersLabel();

    public String getCustomerOrderLabel();

    public String getCustomerOrderStatus();

    public LocalDateTime getCreatedDate();

    public Integer getSalesEmployeeId();

    public Integer getAssignedToEmployeeId();

    public String getCustomerOrderDescription();

    public Integer getCustomerOrderId();

    public Integer getResponsableId();

    public Integer getTiersId();

    public Integer getConfrereId();

    public String getAffaireLabel();

    public Float getTotalPrice();

    public Float getDepositTotalAmount();

    public Integer getQuotationId();

    public String getAffaireSiren();

    public String getAffaireAddress();

    public Integer getAnnouncementNbr();

    public Integer getFormaliteNbr();

    public Integer getBodaccNbr();

    public Integer getDomiciliationNbr();

    public Integer getSimpleProvisionNbr();

    public LocalDateTime getLastStatusUpdate();

    public String getCustomerOrderOriginLabel();
}
