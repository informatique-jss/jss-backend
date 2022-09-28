import { CustomerOrder } from "../../quotation/model/CustomerOrder";
import { Invoice } from "../../quotation/model/Invoice";
import { Payment } from "./Payment";

export interface AssociationSummaryTable {
  payment: Payment;
  customerOrder: CustomerOrder;
  invoice: Invoice;
  amountUsed: number;
}
