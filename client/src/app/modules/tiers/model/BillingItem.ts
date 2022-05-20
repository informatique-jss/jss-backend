import { VatRate } from "../../miscellaneous/model/VatRate";
import { BillingType } from "./BillingType";
import { SpecialOffer } from "./SpecialOffer";

export interface BillingItem {
  id: number;
  discountRate: number;
  discountAmount: number;
  billingType: BillingType;
  vatRate: VatRate;
  specialOffers: SpecialOffer[];
}
