import { InvoiceStatus } from "./InvoiceStatus";

export interface InvoiceSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  invoiceStatus: InvoiceStatus[];
}
