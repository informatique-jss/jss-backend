import { Invoice } from "../../quotation/model/Invoice";
import { AzureInvoice } from "./AzureInvoice";

export interface AzureReceiptInvoiceStatus {
  invoices: Invoice[];
  invoicesStatus: boolean;
  azureInvoices: AzureInvoice[];
  azureInvoicesStatus: boolean;
  paymentStatus: boolean;
  customerInvoicedStatus: boolean;
}
