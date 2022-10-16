import { IReferential } from "../../administration/model/IReferential";
import { Vat } from "./Vat";

export interface BillingType extends IReferential {
  canOverridePrice: boolean;
  isPriceBasedOnCharacterNumber: boolean;
  isOverrideVat: boolean;
  vat: Vat;
  isOptionnal: Vat;
}
