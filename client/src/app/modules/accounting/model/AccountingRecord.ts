import { Payment } from "../../invoicing/model/Payment";
import { CustomerOrder } from "../../quotation/model/CustomerOrder";
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
  letteringDateTime: Date;
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
  customerOrder: CustomerOrder;
  payment: Payment;
  contrePasse: AccountingRecord;
  isANouveau: boolean;
  isFromAs400: boolean;
  isManual: boolean;
}

