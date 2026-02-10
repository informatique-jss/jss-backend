import { AccountingAccount } from "./AccountingAccount";

export interface PaySlipLineType {
  id: number;
  code: string;
  label: string;
  accountingAccountDebit: AccountingAccount;
  accountingAccountCredit: AccountingAccount;
  isNotToUse: boolean;
  isUseEmployerPart: boolean;
}
