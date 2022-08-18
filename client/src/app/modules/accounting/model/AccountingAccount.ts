import { IReferential } from "../../administration/model/IReferential";
import { AccountingAccountClass } from "./AccountingAccountClass";

export interface AccountingAccount extends IReferential {
  accountingAccountNumber: string;
  accountingAccountSubNumber: string;
  accountingAccountClass: AccountingAccountClass;
}
