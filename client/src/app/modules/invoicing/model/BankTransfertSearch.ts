import { Provider } from "../../miscellaneous/model/Provider";

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
}
