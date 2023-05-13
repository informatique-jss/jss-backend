import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { AzureReceiptInvoice } from "./AzureReceiptInvoice";

export interface AzureReceipt extends IAttachment {
  id: number;
  modelUsed: string;
  globalDocumentConfidence: number;
  azureReceiptInvoices: AzureReceiptInvoice[];
  isReconciliated: boolean;
}
