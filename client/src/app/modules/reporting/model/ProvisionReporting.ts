export interface ProvisionReporting {
  customerOrderCreatedDateYear: string;
  invoiceDateMonth: string;
  customerOrderCreatedDateMonth: string;
  customerOrderCreatedDateWeek: string;
  customerOrderCreatedDateDay: string;
  customerOrderStatusLabel: string;
  provisionAssignedToLabel: string;
  provisionFamilyTypeLabel: string;
  provisionTypeLabel: string;
  aggregateProvisionTypeLabel: string;
  provisionNumber: number;
  provisionStatus: string;
  waitedCompetentAuthorityLabel: string;
  turnoverAmountWithoutTax: number;
  turnoverAmountWithTax: number;
  turnoverAmountWithoutDebourWithoutTax: number;
  turnoverAmountWithoutDebourWithTax: number;
}
