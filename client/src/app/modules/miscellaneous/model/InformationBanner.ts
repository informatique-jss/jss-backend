import { IReferential } from "../../administration/model/IReferential";

export interface InformationBanner extends IReferential {
  isActive: boolean;
  text: string;
  label: string;
}
