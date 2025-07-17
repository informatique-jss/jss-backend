import { IReferential } from "../../my-account/model/IReferential";
import { ProvisionFamilyType } from "../../my-account/model/ProvisionFamilyType";
import { AnnouncementNoticeTemplateFragment } from './AnnouncementNoticeTemplateFragment';

export interface AnnouncementNoticeTemplate extends IReferential {
  text: string;
  label: string;
  code: string;
  announcementNoticeTemplateFragments: AnnouncementNoticeTemplateFragment[];
  provisionFamilyTypes: ProvisionFamilyType[];
}
