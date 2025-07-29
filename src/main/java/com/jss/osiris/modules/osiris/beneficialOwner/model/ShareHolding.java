package com.jss.osiris.modules.osiris.beneficialOwner.model;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;

@Embeddable
public class ShareHolding {

    private Boolean shareHoldsMoreThan25Percent;
    private BigDecimal shareTotalPercentage;
    private Boolean isDirectShare;
    private BigDecimal shareFullOwnership;
    private BigDecimal shareBareOwnership;
    private Boolean isIndirectShare;
    private Boolean shareThroughCoOwnership;
    private BigDecimal shareTotalCoOwnerShip;
    private BigDecimal shareFullCoOwnership;
    private BigDecimal shareBareCoOwnership;
    private Boolean shareThroughLegalEntities;
    private BigDecimal shareTotalLegalEntites;
    private BigDecimal shareFullLegalEntities;
    private BigDecimal shareBareLegalEntities;

    public Boolean getShareHoldsMoreThan25Percent() {
        return shareHoldsMoreThan25Percent;
    }

    public void setShareHoldsMoreThan25Percent(Boolean holdsMoreThan25Percent) {
        this.shareHoldsMoreThan25Percent = holdsMoreThan25Percent;
    }

    public BigDecimal getShareTotalPercentage() {
        return shareTotalPercentage;
    }

    public void setShareTotalPercentage(BigDecimal totalPercentage) {
        this.shareTotalPercentage = totalPercentage;
    }

    public Boolean getIsDirectShare() {
        return isDirectShare;
    }

    public void setIsDirectShare(Boolean isDirect) {
        this.isDirectShare = isDirect;
    }

    public BigDecimal getShareFullOwnership() {
        return shareFullOwnership;
    }

    public void setShareFullOwnership(BigDecimal fullOwnership) {
        this.shareFullOwnership = fullOwnership;
    }

    public BigDecimal getShareBareOwnership() {
        return shareBareOwnership;
    }

    public void setShareBareOwnership(BigDecimal bareOwnership) {
        this.shareBareOwnership = bareOwnership;
    }

    public Boolean getIsIndirectShare() {
        return isIndirectShare;
    }

    public void setIsIndirectShare(Boolean isIndirect) {
        this.isIndirectShare = isIndirect;
    }

    public Boolean getShareThroughCoOwnership() {
        return shareThroughCoOwnership;
    }

    public void setShareThroughCoOwnership(Boolean throughCoOwnership) {
        this.shareThroughCoOwnership = throughCoOwnership;
    }

    public BigDecimal getShareTotalCoOwnerShip() {
        return shareTotalCoOwnerShip;
    }

    public void setShareTotalCoOwnerShip(BigDecimal totalCoOwnerShip) {
        this.shareTotalCoOwnerShip = totalCoOwnerShip;
    }

    public BigDecimal getShareFullCoOwnership() {
        return shareFullCoOwnership;
    }

    public void setShareFullCoOwnership(BigDecimal fullCoOwnership) {
        this.shareFullCoOwnership = fullCoOwnership;
    }

    public BigDecimal getShareBareCoOwnership() {
        return shareBareCoOwnership;
    }

    public void setShareBareCoOwnership(BigDecimal bareCoOwnership) {
        this.shareBareCoOwnership = bareCoOwnership;
    }

    public Boolean getShareThroughLegalEntities() {
        return shareThroughLegalEntities;
    }

    public void setShareThroughLegalEntities(Boolean throughLegalEntities) {
        this.shareThroughLegalEntities = throughLegalEntities;
    }

    public BigDecimal getShareTotalLegalEntites() {
        return shareTotalLegalEntites;
    }

    public void setShareTotalLegalEntites(BigDecimal totalLegalEntites) {
        this.shareTotalLegalEntites = totalLegalEntites;
    }

    public BigDecimal getShareFullLegalEntities() {
        return shareFullLegalEntities;
    }

    public void setShareFullLegalEntities(BigDecimal fullLegalEntities) {
        this.shareFullLegalEntities = fullLegalEntities;
    }

    public BigDecimal getShareBareLegalEntities() {
        return shareBareLegalEntities;
    }

    public void setShareBareLegalEntities(BigDecimal bareLegalEntities) {
        this.shareBareLegalEntities = bareLegalEntities;
    }

}