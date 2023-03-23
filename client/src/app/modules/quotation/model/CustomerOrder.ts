import { Deposit } from "../../invoicing/model/Deposit";
import { Payment } from "../../invoicing/model/Payment";
import { CustomerOrderStatus } from './CustomerOrderStatus';
import { IQuotation } from "./IQuotation";

export interface CustomerOrder extends IQuotation {
  deposits: Deposit[];
  payments: Payment[];
  customerOrderStatus: CustomerOrderStatus;
  centralPayPendingPaymentAmount: number;
}
