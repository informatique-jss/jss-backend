import { IReferential } from "../../administration/model/IReferential";

export interface CompetentAuthorityType extends IReferential {
  isDirectCharge: boolean;
  isToReminder: boolean;
}
