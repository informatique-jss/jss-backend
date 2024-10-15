import { BillingLabelType } from "./BillingLabelType";

export interface InvoicingSummary {
  totalPrice: number;
  discountTotal: number;
  preTaxPriceTotal: number;
  vatTotal: number;
  billingLabelType: BillingLabelType;
}
