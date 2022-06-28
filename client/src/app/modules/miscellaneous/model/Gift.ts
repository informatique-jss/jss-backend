import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";

export interface Gift extends IReferential {
  stock: number;
  accountingAccount: AccountingAccount;
}
