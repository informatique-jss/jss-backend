import { AccountingAccount } from "./AccountingAccount";
import { IReferential } from "./IReferential";

export interface Vat extends IReferential {
  rate: number;
  accountingAccount: AccountingAccount;
}
