import { AssoSpecialOfferBillingType } from "./AssoSpecialOfferBillingType";
import { BillingType } from "./BillingType";

export interface BillingItem {
  id: number | undefined;
  billingType: BillingType;
  preTaxPrice: number;
  startDate: Date;
  assoSpecialOfferBillingItem: AssoSpecialOfferBillingType[];
}
