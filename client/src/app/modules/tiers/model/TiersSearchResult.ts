export interface TiersSearchResult {
  tiersLabel: string;
  tiersCategory: string;
  tiersId: number;
  salesEmployeeLabel: string;
  salesEmployeeId: number;
  formalisteLabel: string;
  formalisteId: number;
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
  isNewTiers: boolean;
}
