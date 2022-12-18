import { IReferential } from "../../administration/model/IReferential";
import { PrincipalAccountingAccount } from './PrincipalAccountingAccount';

export interface AccountingAccount extends IReferential {
  principalAccountingAccount: PrincipalAccountingAccount;
  accountingAccountSubNumber: number;
  isViewRestricted: boolean;
}
