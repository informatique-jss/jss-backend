import { AccountingAccount } from "./AccountingAccount";
import { AccountingAccountClass } from "./AccountingAccountClass";

export interface AccountingBalanceSearch {
  accountingClass: AccountingAccountClass;
  accountingAccount: AccountingAccount | undefined;
  accountingAccountNumber: string | undefined;
  startDate: Date | undefined;
  endDate: Date | undefined;
}
