import { AccountingRecord } from "./AccountingRecord";

export interface SageRecord {
  id: number;
  targetAccountingAccount: number;
  operationDate: Date;
  label: string;
  creditOrDebit: string;
  amount: number;
  createdDate: Date;
  accountingRecords: AccountingRecord[];
}
