import { AccountingAccount } from "../../accounting/model/AccountingAccount";
import { IReferential } from "../../administration/model/IReferential";

export interface Provider extends IReferential {
  id: number;
  label: string;
  accountingAccountCustomer: AccountingAccount;
  accountingAccountProvider: AccountingAccount;
  accountingAccountDeposit: AccountingAccount;
  iban: string;
  bic: string;
}
