import { Service } from "../../my-account/model/Service";

export interface NoticeTemplateDescription {
  service: Service;
  isShowNoticeTemplate: boolean;
  displayText: string;
  announcementOrder: number;
  isUsingTemplate: Boolean;
}
