import { AzureReceiptInvoiceStatus } from "./AzureReceiptInvoiceStatus";

export interface AzureReceiptInvoice {
  id: number;
  invoiceId: string;
  invoiceTotal: number;
  isReconciliated: boolean;
  azureReceiptInvoiceStatus: AzureReceiptInvoiceStatus;
}
