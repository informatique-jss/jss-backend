export interface ResponsableSearchResult {
  tiersLabel: string;
  tiersCategory: string;
  responsableLabel: string;
  responsableCategory: string;
  responsableId: number;
  tiersId: number;
  salesEmployeeLabel: string;
  salesEmployeeId: number;
  firstOrderDay: Date;
  lastOrderDay: Date;
  createdDateDay: Date;
  lastResponsableFollowupDate: Date;
  announcementJssNbr: number;
  announcementConfrereNbr: number;
  announcementNbr: number;
  formalityNbr: number;
  billingLabelType: string;
  turnoverAmountWithoutTax: number;
  turnoverAmountWithTax: number;
  turnoverAmountWithoutDebourWithoutTax: number;
  turnoverAmountWithoutDebourWithTax: number;
  confrere: string;
}
