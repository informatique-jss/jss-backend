import { IReferential } from "../../administration/model/IReferential";
import { AssoSpecialOfferBillingType } from "./AssoSpecialOfferBillingType";

export interface SpecialOffer extends IReferential {
  customLabel: string;
  assoSpecialOfferBillingTypes: AssoSpecialOfferBillingType[];
}
