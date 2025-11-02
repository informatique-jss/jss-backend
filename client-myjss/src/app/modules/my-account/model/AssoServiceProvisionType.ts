import { AnnouncementNoticeTemplate } from "../../quotation/model/AnnouncementNoticeTemplate";

export interface AssoServiceProvisionType {
  id: number;
  announcementNoticeTemplates: AnnouncementNoticeTemplate[];
}
