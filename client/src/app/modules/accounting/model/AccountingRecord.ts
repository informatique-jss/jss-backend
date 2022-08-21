import { Invoice } from "../../quotation/model/Invoice";
import { InvoiceItem } from "../../quotation/model/InvoiceItem";
import { AccountingAccount } from "./AccountingAccount";
import { AccountingJournal } from "./AccountingJournal";

export interface AccountingRecord {
  id: number;
  accountingDateTime: Date;
  operationDateTime: Date;
  manualAccountingDocumentNumber: string;
  manualAccountingDocumentDate: Date;
  manualAccountingDocumentDeadline: Date;
  label: string;
  creditAmount: number;
  debitAmount: number;
  accountingAccount: AccountingAccount;
  isTemporary: boolean;
  invoiceItem: InvoiceItem;
  invoice: Invoice;
  accountingJournal: AccountingJournal;
  operationId: number;
  letteringNumber: number;
  letteringDate: Date;
  /**
   * only used in fronted
   */
  creditAccumulation: number;
  /**
  * only used in fronted
  */
  debitAccumulation: number;
  /**
  * only used in fronted
  */
  balance: number;
}
