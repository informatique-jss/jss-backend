import { BillingType } from "./BillingType";
import { SpecialOffer } from "./SpecialOffer";

export interface AssoSpecialOfferBillingType {
  id: number;
  specialOffer: SpecialOffer;
  billingType: BillingType;
  discountRate: number;
  discountAmount: number;
}
