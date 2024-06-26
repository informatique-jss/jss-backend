import { AccountingAccount } from "./AccountingAccount";
import { AccountingAccountClass } from "./AccountingAccountClass";
import { PrincipalAccountingAccount } from './PrincipalAccountingAccount';

export interface AccountingBalanceSearch {
  accountingClass: AccountingAccountClass;
  accountingAccount: AccountingAccount | undefined;
  principalAccountingAccounts: PrincipalAccountingAccount[] | undefined;
  startDate: Date | undefined;
  endDate: Date | undefined;
  isFromAs400: boolean;
}
