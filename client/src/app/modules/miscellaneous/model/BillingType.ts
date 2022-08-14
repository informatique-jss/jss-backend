import { IReferential } from "../../administration/model/IReferential";

export interface BillingType extends IReferential {
  canOverridePrice: boolean;
  isPriceBasedOnCharacterNumber: boolean;
}
