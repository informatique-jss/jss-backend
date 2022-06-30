import { IReferential } from "../../administration/model/IReferential";
import { BillingType } from "../../miscellaneous/model/BillingType";
import { ProvisionFamilyType } from "./ProvisionFamilyType";

export interface ProvisionType extends IReferential {
  provisionFamilyType: ProvisionFamilyType;
  billingTypes: BillingType[];
}
