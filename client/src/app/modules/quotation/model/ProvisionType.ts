import { IReferential } from "../../administration/model/IReferential";
import { BillingType } from "../../miscellaneous/model/BillingType";
import { Employee } from "../../profile/model/Employee";
import { AssignationType } from "./AssignationType";
import { ProvisionFamilyType } from "./ProvisionFamilyType";
import { ProvisionScreenType } from "./ProvisionScreenType";

export interface ProvisionType extends IReferential {
  provisionFamilyType: ProvisionFamilyType;
  billingTypes: BillingType[];
  provisionScreenType: ProvisionScreenType;
  assignationType: AssignationType;
  defaultEmployee: Employee;
}
