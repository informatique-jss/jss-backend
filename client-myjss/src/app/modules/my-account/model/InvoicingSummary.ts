import { BillingLabelType } from "./BillingLabelType";

export interface InvoicingSummary {
  totalPrice: number;
  discountTotal: number;
  preTaxPriceTotal: number;
  vatTotal: number;
  remainingToPay: number;
  billingLabelType: BillingLabelType;
}
