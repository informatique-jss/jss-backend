import { ITiers } from "../../tiers/model/ITiers";
import { InvoiceStatus } from "./InvoiceStatus";

export interface InvoiceSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  invoiceStatus: InvoiceStatus[];
  showToRecover: boolean;
  customerOrders: ITiers[];
}
