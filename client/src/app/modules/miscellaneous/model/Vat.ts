import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";

export interface Vat extends IReferential {
  rate: number;
  accountingAccount: AccountingAccount;
}
