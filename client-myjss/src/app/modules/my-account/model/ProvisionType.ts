import { Employee } from "../../profile/model/Employee";
import { AssignationType } from "./AssignationType";
import { BillingType } from "./BillingType";
import { CompetentAuthority } from "./CompetentAuthority";
import { CustomerOrderFrequency } from "./CustomerOrderFrequency";
import { IReferential } from "./IReferential";
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
  recurringFrequency: CustomerOrderFrequency;
}
