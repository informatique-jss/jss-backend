import { AssoAffaireOrder } from '../../my-account/model/AssoAffaireOrder';
import { Service } from "../../my-account/model/Service";
import { AnnouncementNoticeTemplate } from "./AnnouncementNoticeTemplate";

export interface NoticeTemplateDescription {
  service: Service;
  isShowNoticeTemplate: boolean;
  displayText: string;
  announcementOrder: number;
  isUsingTemplate: Boolean;
  selectedTemplate: AnnouncementNoticeTemplate | undefined;
  assoAffaireOrder: AssoAffaireOrder;
}
