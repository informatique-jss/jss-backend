import { IReferential } from "../../administration/model/IReferential";
import { BillingType } from "../../miscellaneous/model/BillingType";
import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { CustomerOrderFrequency } from "../../miscellaneous/model/CustomerOrderFrequency";
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
  assignationWeight: number;
  defaultCompetentAuthorityServiceProvider: CompetentAuthority;
  isDisplayActeDepositScreen: boolean;
  isDisplayAnnualAccountScreen: boolean;
  isRecurring: boolean;
  isMergeable: boolean;
  recurringFrequency: CustomerOrderFrequency;

}
