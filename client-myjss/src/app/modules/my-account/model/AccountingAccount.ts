import { IReferential } from "./IReferential";
import { PrincipalAccountingAccount } from "./PrincipalAccountingAccount";

export interface AccountingAccount extends IReferential {
  principalAccountingAccount: PrincipalAccountingAccount;
  accountingAccountSubNumber: number;
  isViewRestricted: boolean;
  isAllowedToPutIntoAccount: boolean;
}
