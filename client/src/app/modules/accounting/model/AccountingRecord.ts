import { AccountingAccount } from "./AccountingAccount";

export interface AccountingRecord {
  id: number;
  accountingDate: Date;
  operationDate: Date;
  manualAccountingDocumentNumber: string;
  creditAmount: number;
  debitAmount: number;
  accountingAccount: AccountingAccount;
}
