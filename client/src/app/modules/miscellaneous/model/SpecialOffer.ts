import { IReferential } from "../../administration/model/IReferential";
import { BillingItem } from "./BillingItem";

export interface SpecialOffer extends IReferential {
  billingItems: BillingItem[];
}
