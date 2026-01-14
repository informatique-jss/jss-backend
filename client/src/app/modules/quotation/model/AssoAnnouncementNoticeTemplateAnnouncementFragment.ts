import { AnnouncementNoticeTemplate } from "./AnnouncementNoticeTemplate";
import { AnnouncementNoticeTemplateFragment } from "./AnnouncementNoticeTemplateFragment";

export interface AssoAnnouncementNoticeTemplateAnnouncementFragment {
    id: number;
    announcementNoticeTemplate: AnnouncementNoticeTemplate;
    announcementNoticeTemplateFragment: AnnouncementNoticeTemplateFragment;
    isMandatory: boolean;
}