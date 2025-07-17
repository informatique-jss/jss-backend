import { IReferential } from "../../my-account/model/IReferential";

export interface AnnouncementNoticeTemplateFragment extends IReferential {
  text: string;
  label: string;
  code: string;
  isMultiple: boolean;
}
