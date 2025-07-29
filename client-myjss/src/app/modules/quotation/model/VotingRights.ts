export interface VotingRights {
  votingHoldsMoreThan25Percent: boolean;
  votingTotalPercentage: number;
  isDirectVoting: boolean;
  votingFullOwnership: number;
  votingBareOwnership: number;
  usufruct: number;
  isIndirectVoting: boolean;
  votingThroughCoOwnership: boolean;
  votingTotalCoOwnerShip: number;
  votingFullCoOwnership: number;
  votingBareCoOwnership: number;
  usufructCoOwnerShip: number;
  votingThroughLegalEntities: boolean;
  votingTotalLegalEntites: number;
  votingFullLegalEntities: number;
  votingBareLegalEntities: number;
  usufructLegalEntities: number;
}
