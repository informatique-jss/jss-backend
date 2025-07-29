package com.jss.osiris.modules.osiris.beneficialOwner.model;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;

@Embeddable
public class VotingRights {

    private Boolean votingHoldsMoreThan25Percent;
    private BigDecimal votingTotalPercentage;
    private Boolean isDirectVoting;
    private BigDecimal votingFullOwnership;
    private BigDecimal votingBareOwnership;
    private BigDecimal usufruct;
    private Boolean isIndirectVoting;
    private Boolean votingThroughCoOwnership;
    private BigDecimal votingTotalCoOwnerShip;
    private BigDecimal votingFullCoOwnership;
    private BigDecimal votingBareCoOwnership;
    private BigDecimal usufructCoOwnerShip;
    private Boolean votingThroughLegalEntities;
    private BigDecimal votingTotalLegalEntites;
    private BigDecimal votingFullLegalEntities;
    private BigDecimal votingBareLegalEntities;
    private BigDecimal usufructLegalEntities;

    public Boolean getVotingHoldsMoreThan25Percent() {
        return votingHoldsMoreThan25Percent;
    }

    public void setVotingHoldsMoreThan25Percent(Boolean holdsMoreThan25Percent) {
        this.votingHoldsMoreThan25Percent = holdsMoreThan25Percent;
    }

    public BigDecimal getVotingTotalPercentage() {
        return votingTotalPercentage;
    }

    public void setVotingTotalPercentage(BigDecimal totalPercentage) {
        this.votingTotalPercentage = totalPercentage;
    }

    public Boolean getIsDirectVoting() {
        return isDirectVoting;
    }

    public void setIsDirectVoting(Boolean isDirect) {
        this.isDirectVoting = isDirect;
    }

    public BigDecimal getVotingFullOwnership() {
        return votingFullOwnership;
    }

    public void setVotingFullOwnership(BigDecimal fullOwnership) {
        this.votingFullOwnership = fullOwnership;
    }

    public BigDecimal getVotingBareOwnership() {
        return votingBareOwnership;
    }

    public void setVotingBareOwnership(BigDecimal bareOwnership) {
        this.votingBareOwnership = bareOwnership;
    }

    public BigDecimal getUsufruct() {
        return usufruct;
    }

    public void setUsufruct(BigDecimal usufruct) {
        this.usufruct = usufruct;
    }

    public Boolean getIsIndirectVoting() {
        return isIndirectVoting;
    }

    public void setIsIndirectVoting(Boolean isIndirect) {
        this.isIndirectVoting = isIndirect;
    }

    public Boolean getVotingThroughCoOwnership() {
        return votingThroughCoOwnership;
    }

    public void setVotingThroughCoOwnership(Boolean throughCoOwnership) {
        this.votingThroughCoOwnership = throughCoOwnership;
    }

    public BigDecimal getVotingTotalCoOwnerShip() {
        return votingTotalCoOwnerShip;
    }

    public void setVotingTotalCoOwnerShip(BigDecimal totalCoOwnerShip) {
        this.votingTotalCoOwnerShip = totalCoOwnerShip;
    }

    public BigDecimal getVotingFullCoOwnership() {
        return votingFullCoOwnership;
    }

    public void setVotingFullCoOwnership(BigDecimal fullCoOwnership) {
        this.votingFullCoOwnership = fullCoOwnership;
    }

    public BigDecimal getVotingBareCoOwnership() {
        return votingBareCoOwnership;
    }

    public void setVotingBareCoOwnership(BigDecimal bareCoOwnership) {
        this.votingBareCoOwnership = bareCoOwnership;
    }

    public BigDecimal getUsufructCoOwnerShip() {
        return usufructCoOwnerShip;
    }

    public void setUsufructCoOwnerShip(BigDecimal usufructCoOwnerShip) {
        this.usufructCoOwnerShip = usufructCoOwnerShip;
    }

    public Boolean getVotingThroughLegalEntities() {
        return votingThroughLegalEntities;
    }

    public void setVotingThroughLegalEntities(Boolean throughLegalEntities) {
        this.votingThroughLegalEntities = throughLegalEntities;
    }

    public BigDecimal getVotingTotalLegalEntites() {
        return votingTotalLegalEntites;
    }

    public void setVotingTotalLegalEntites(BigDecimal totalLegalEntites) {
        this.votingTotalLegalEntites = totalLegalEntites;
    }

    public BigDecimal getVotingFullLegalEntities() {
        return votingFullLegalEntities;
    }

    public void setVotingFullLegalEntities(BigDecimal fullLegalEntities) {
        this.votingFullLegalEntities = fullLegalEntities;
    }

    public BigDecimal getVotingBareLegalEntities() {
        return votingBareLegalEntities;
    }

    public void setVotingBareLegalEntities(BigDecimal bareLegalEntities) {
        this.votingBareLegalEntities = bareLegalEntities;
    }

    public BigDecimal getUsufructLegalEntities() {
        return usufructLegalEntities;
    }

    public void setUsufructLegalEntities(BigDecimal usufructLegalEntities) {
        this.usufructLegalEntities = usufructLegalEntities;
    }

}
