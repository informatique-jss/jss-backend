import { Employee } from "../../profile/model/Employee";
import { Tiers } from "../../tiers/model/Tiers";
import { InvoiceStatus } from "./InvoiceStatus";

export interface InvoiceSearch {
  startDate: Date | undefined;
  endDate: Date | undefined;
  minAmount: number;
  maxAmount: number;
  invoiceStatus: InvoiceStatus[];
  showToRecover: boolean;
  customerOrders: Tiers[];
  invoiceId: number;
  customerOrderId: number;
  affaireId: number;
  salesEmployee: Employee;
}
