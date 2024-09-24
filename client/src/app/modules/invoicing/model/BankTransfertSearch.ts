import { Provider } from "@angular/core";

export interface BankTransfertSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  label: string;
  isHideExportedBankTransfert: boolean;
  isDisplaySelectedForExportBankTransfert: boolean;
  idBankTransfert: number;
  provider: Provider;
  isOverrideExecutionDate: boolean;
}
