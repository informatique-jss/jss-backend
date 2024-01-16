import { Payment } from "../../invoicing/model/Payment";
import { Refund } from "../../invoicing/model/Refund";
import { CustomerOrderStatus } from './CustomerOrderStatus';
import { IQuotation } from "./IQuotation";

export interface CustomerOrder extends IQuotation {
  payments: Payment[];
  customerOrderStatus: CustomerOrderStatus;
  isGifted: boolean;
  refunds: Refund[];
}
