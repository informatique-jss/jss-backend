package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface OrderingSearchResult {
    public String getTiersLabel();

    public String getCustomerOrderLabel();

    public String getCustomerOrderStatus();

    public LocalDateTime getCreatedDate();

    public LocalDateTime getProductionEffectiveDate();

    public Integer getSalesEmployeeId();

    public String getCustomerOrderDescription();

    public Integer getCustomerOrderId();

    public Integer getResponsableId();

    public Integer getTiersId();

    public String getAffaireLabel();

    public String getServiceTypeLabel();

    public Float getTotalPrice();

    public Float getDepositTotalAmount();

    public Integer getQuotationId();

    public String getAffaireSiren();

    public String getAffaireAddress();

    public LocalDateTime getLastStatusUpdate();

    public String getCustomerOrderOriginLabel();

    public String getProvisionStatus();

    // Recurring

    public Integer getCustomerOrderParentRecurringId();

    public LocalDate getRecurringPeriodStartDate();

    public LocalDate getRecurringPeriodEndDate();

    public LocalDate getRecurringStartDate();

    public LocalDate getRecurringEndDate();

    public Boolean getIsRecurringAutomaticallyBilled();
}
