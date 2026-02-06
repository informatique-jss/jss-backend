import { AccountingAccount } from "./AccountingAccount";

export interface PaySlipLineType {
  id: number;
  code: string;
  label: string;
  accountingAccount: AccountingAccount;
  isOnCredit: boolean;
  isNotToUse: boolean;
}
