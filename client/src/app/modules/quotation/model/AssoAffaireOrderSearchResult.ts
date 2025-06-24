
export interface AssoAffaireOrderSearchResult {
  affaireLabel: string;
  affaireAddress: string;
  tiersLabel: string;
  responsableLabel: string;
  confrereLabel: string;
  provisionTypeLabel: string;
  statusLabel: string;
  assoId: number;
  customerOrderId: number;
  provisionId: number;
  isEmergency: boolean;
  waitedCompetentAuthorityLabel: string;
  competentAuthorityLabel: string;
  provisionStatusDatetime: Date;
  provisionCreatedDatetime: Date;
  createdDate: Date;
  serviceTypeLabel: string;
}
