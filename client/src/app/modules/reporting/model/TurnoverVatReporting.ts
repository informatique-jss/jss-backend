export interface TurnoverVatReporting {
  invoiceDateYear: string;
  invoiceDateMonth: string;
  invoiceDateWeek: string;
  invoiceDateDay: string;
  turnoverAmountWithoutTax: number;
  turnoverAmountWithTax: number;
  turnoverAmountWithoutDebourWithoutTax: number;
  turnoverAmountWithoutDebourWithTax: number;
  tiersLabel: string;
  tiersCategory: string;
  invoiceCreator: string;
  salesEmployeeLabel: string;
  invoiceStatusLabel: string;
  vatLabel: string;
}
