import { IReferential } from "../../administration/model/IReferential";
import { AnnouncementNoticeTemplateFragment } from "./AnnouncementNoticeTemplateFragment";
import { ProvisionFamilyType } from "./ProvisionFamilyType";

export interface AnnouncementNoticeTemplate extends IReferential {
  text: string;
  provisionFamilyTypes: ProvisionFamilyType[];
  announcementNoticeTemplateFragments: AnnouncementNoticeTemplateFragment[];
}
