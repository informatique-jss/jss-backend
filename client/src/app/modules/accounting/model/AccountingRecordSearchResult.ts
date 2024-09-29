
export interface AccountingRecordSearchResult {
  recordId: number;
  operationId: number;
  accountingDateTime: Date;
  operationDateTime: Date;
  accountingJournalLabel: string;
  accountingJournalCode: string;
  principalAccountingAccountCode: string;
  accountingAccountSubNumber: string;
  accountingAccountLabel: string;
  manualAccountingDocumentNumber: string;
  manualAccountingDocumentDate: Date;
  debitAmount: number;
  creditAmount: number;
  label: string;
  letteringNumber: number;
  letteringDate: Date;
  invoiceId: number;
  customerId: number;
  paymentId: number;
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
  affaireLabel: string;
  responsable: string;
  isTemporary: boolean;
  temporaryOperationId: number;
  isFromAs400: boolean;
  isManual: boolean;
}
