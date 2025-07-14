import { IReferential } from "./IReferential";
import { ProvisionScreenType } from "./ProvisionScreenType";

export interface ProvisionType extends IReferential {
  provisionScreenType: ProvisionScreenType;
}
