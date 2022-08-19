import { AccountingAccount } from "./AccountingAccount";

export interface AccountingRecord {
  id: number;
  accountingDateTime: Date;
  operationDateTime: Date;
  manualAccountingDocumentNumber: string;
  creditAmount: number;
  debitAmount: number;
  accountingAccount: AccountingAccount;
}
