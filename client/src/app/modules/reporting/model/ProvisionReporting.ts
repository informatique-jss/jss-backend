export interface ProvisionReporting {
  customerOrderCreatedDateYear: string;
  customerOrderCreatedDateMonth: string;
  customerOrderCreatedDateWeek: string;
  customerOrderCreatedDateDay: string;
  customerOrderStatusLabel: string;
  provisionAssignedToLabel: string;
  provisionFamilyTypeLabel: string;
  aggregateProvisionTypeLabel: string;
  provisionNumber: number;
  provisionStatus: string;
  waitedCompetentAuthorityLabel: string;
  turnoverAmountWithoutTax: number;
  turnoverAmountWithTax: number;
  turnoverAmountWithoutDebourWithoutTax: number;
  turnoverAmountWithoutDebourWithTax: number;
}
