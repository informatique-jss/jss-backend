import { IReferential } from "../../administration/model/IReferential";
import { ProvisionFamilyType } from "./ProvisionFamilyType";

export interface AnnouncementNoticeTemplate extends IReferential {
  text: string;
  provisionFamilyTypes: ProvisionFamilyType[];
}
