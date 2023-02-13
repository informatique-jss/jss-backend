package com.jss.osiris.modules.invoicing.model;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

public class DebourSearch {
    private CompetentAuthority competentAuthority;
    private Float minAmount;
    private Float maxAmount;
    private Boolean isNonAssociated;
    private Boolean isCompetentAuthorityDirectCharge;
    private IndexEntity customerOrder;

    public CompetentAuthority getCompetentAuthority() {
        return competentAuthority;
    }

    public void setCompetentAuthority(CompetentAuthority competentAuthority) {
        this.competentAuthority = competentAuthority;
    }

    public Float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Float minAmount) {
        this.minAmount = minAmount;
    }

    public Float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Float maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Boolean getIsNonAssociated() {
        return isNonAssociated;
    }

    public void setIsNonAssociated(Boolean isNonAssociated) {
        this.isNonAssociated = isNonAssociated;
    }

    public Boolean getIsCompetentAuthorityDirectCharge() {
        return isCompetentAuthorityDirectCharge;
    }

    public void setIsCompetentAuthorityDirectCharge(Boolean isCompetentAuthorityDirectCharge) {
        this.isCompetentAuthorityDirectCharge = isCompetentAuthorityDirectCharge;
    }

    public IndexEntity getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(IndexEntity customerOrder) {
        this.customerOrder = customerOrder;
    }

}
