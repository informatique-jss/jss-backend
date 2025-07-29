export interface ShareHolding {
  shareHoldsMoreThan25Percent: boolean;
  shareTotalPercentage: number;
  isDirectShare: boolean;
  shareFullOwnership: number;
  shareBareOwnership: number;
  isIndirectShare: boolean;
  shareThroughCoOwnership: boolean;
  shareTotalCoOwnerShip: number;
  shareFullCoOwnership: number;
  shareBareCoOwnership: number;
  shareThroughLegalEntities: boolean;
  shareTotalLegalEntites: number;
  shareFullLegalEntities: number;
  shareBareLegalEntities: number;
}
