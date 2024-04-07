import { Payment } from "../../invoicing/model/Payment";
import { Refund } from "../../invoicing/model/Refund";
import { CustomerOrderFrequency } from "../../miscellaneous/model/CustomerOrderFrequency";
import { CustomerOrderStatus } from './CustomerOrderStatus';
import { IQuotation } from "./IQuotation";

export interface CustomerOrder extends IQuotation {
  payments: Payment[];
  customerOrderStatus: CustomerOrderStatus;
  isGifted: boolean;
  refunds: Refund[];
  isRecurring: boolean;
  hasCustomerOrderParent: boolean;
  hasCustomerOrderParentRecurring: boolean;
  recurringPeriodStartDate: Date;
  recurringPeriodEndDate: Date;
  isRecurringAutomaticallyBilled: Date;
  customerOrderFrequency: CustomerOrderFrequency;
  recurringStartDate: Date;
  recurringEndDate: Date;
}
