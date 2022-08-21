import { AccountingAccount } from "./AccountingAccount";
import { AccountingAccountClass } from "./AccountingAccountClass";
import { AccountingJournal } from "./AccountingJournal";

export interface AccountingRecordSearch {
  accountingClass: AccountingAccountClass;
  accountingAccount: AccountingAccount | undefined;
  accountingJournal: AccountingJournal | undefined;
  startDate: Date | undefined;
  endDate: Date | undefined;
}
