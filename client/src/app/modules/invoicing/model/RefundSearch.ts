
export interface RefundSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  label: string;
  isHideMatchedRefunds: boolean;
  isHideExportedRefunds: boolean;
  idRefund: number;
}
