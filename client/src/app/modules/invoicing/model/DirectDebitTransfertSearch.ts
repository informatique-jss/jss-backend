
export interface DirectDebitTransfertSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  label: string;
  isHideExportedDirectDebitTransfert: boolean;
  idDirectDebitTransfert: number;
}
