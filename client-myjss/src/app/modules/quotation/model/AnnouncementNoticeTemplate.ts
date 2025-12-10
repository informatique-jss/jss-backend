import { IReferential } from "../../my-account/model/IReferential";
import { ProvisionFamilyType } from "../../my-account/model/ProvisionFamilyType";

export interface AnnouncementNoticeTemplate extends IReferential {
  text: string;
  label: string;
  code: string;
  provisionFamilyTypes: ProvisionFamilyType[];
}
