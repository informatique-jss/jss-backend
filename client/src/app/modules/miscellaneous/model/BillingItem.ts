import { IReferential } from "../../administration/model/IReferential";
import { BillingType } from "./BillingType";
import { SpecialOffer } from "./SpecialOffer";
import { Vat } from "./Vat";

export interface BillingItem extends IReferential {
  id: number;
  discountRate: number;
  discountAmount: number;
  billingType: BillingType;
  vat: Vat;
  specialOffers: SpecialOffer[];
}
