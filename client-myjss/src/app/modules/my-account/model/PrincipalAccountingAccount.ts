import { AccountingAccountClass } from "./AccountingAccountClass";
import { IReferential } from "./IReferential";

export interface PrincipalAccountingAccount extends IReferential {
  accountingAccountClass: AccountingAccountClass;
}
